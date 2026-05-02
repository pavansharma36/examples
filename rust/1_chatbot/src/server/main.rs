use std::collections::HashMap;
use std::fmt::{Display, Formatter};
use std::io::{Read, Write};
use std::net::{TcpListener, TcpStream};
use std::ops::Deref;
use std::sync::mpsc::Receiver;
use std::sync::mpsc::Sender;
use std::sync::{Arc, LazyLock};
use std::{env, thread};

static SAFE_MODE: LazyLock<bool> = LazyLock::new(|| match env::var("SAFE_MODE") {
    Ok(value) => value.to_ascii_lowercase() == "true",
    Err(_) => false,
});

struct Sensitive<T>(T);

impl<T> Sensitive<T> {
    fn new(value: T) -> Self {
        Self(value)
    }
}

impl<T: Display> Display for Sensitive<T> {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        if *SAFE_MODE {
            write!(f, "******")
        } else {
            self.0.fmt(f)
        }
    }
}

struct Client {
    conn: Arc<TcpStream>,
}

enum Message {
    ClientConnected(Client),
    ClientDisconnected(Client),
    NewMessage(Client, Vec<u8>),
}

fn handle_client(stream: Arc<TcpStream>, message_sender: Sender<Message>) {
    // Keep the stream in scope by looping indefinitely within the handler
    message_sender
        .send(Message::ClientConnected(Client {
            conn: stream.clone(),
        }))
        .unwrap();
    loop {
        let mut buffer: Vec<u8> = vec![0; 128];
        // Read data from the stream
        match stream.as_ref().read(&mut buffer) {
            Ok(0) => {
                // Connection closed by client
                println!("Client disconnected.");
                message_sender
                    .send(Message::ClientDisconnected(Client {
                        conn: stream.clone(),
                    }))
                    .expect("TODO: panic message");
                break;
            }
            Ok(n) => {
                // Data received, process it and potentially write a response

                // Echo the data back to the client as an example
                message_sender
                    .send(Message::NewMessage(
                        Client {
                            conn: stream.clone(),
                        },
                        buffer[..n].to_vec(),
                    ))
                    .expect("TODO: panic message");
            }
            Err(e) => {
                eprintln!("Error reading from stream: {}", e);
                message_sender
                    .send(Message::ClientConnected(Client {
                        conn: stream.clone(),
                    }))
                    .expect("Error reading message {e}");
                break; // Exit the loop on read error
            }
        }
    }
    // The stream goes out of scope here and is closed automatically.
}

fn handle_server(receiver: Receiver<Message>) -> () {
    let mut clients = HashMap::new();
    loop {
        match receiver.recv() {
            Ok(message) => match message {
                Message::ClientConnected(client) => {
                    println!(
                        "Client connected: {}",
                        Sensitive::new(client.conn.peer_addr().unwrap())
                    );
                    clients.insert(client.conn.clone().peer_addr().unwrap(), client.conn);
                }
                Message::ClientDisconnected(client) => {
                    println!("Client disconnected: {}", client.conn.peer_addr().unwrap());
                    clients.remove(&client.conn.peer_addr().unwrap());
                }
                Message::NewMessage(client, message) => {
                    println!(
                        "Received message from {}: {}",
                        client.conn.peer_addr().unwrap(),
                        String::from_utf8_lossy(&message)
                    );
                    for (addr, conn) in clients.iter() {
                        if client.conn.peer_addr().unwrap() != *addr {
                            let _ = conn.as_ref().write(&message);
                        }
                    }
                }
            },
            Err(_) => panic!("Server error:"),
        }
    }
}

fn main() -> std::io::Result<()> {
    let listener = TcpListener::bind("0.0.0.0:8081")?;
    println!("Listening on {}", Sensitive::new(listener.local_addr()?));

    let (message_sender, message_receiver): (Sender<Message>, Receiver<Message>) =
        std::sync::mpsc::channel();

    thread::spawn(|| handle_server(message_receiver));
    // accept connections and process them serially
    for stream in listener.incoming() {
        match stream {
            Ok(stream) => {
                let message_sender = message_sender.clone();
                thread::spawn(|| {
                    handle_client(Arc::new(stream), message_sender);
                });
            }
            Err(e) => println!("{:?}", e),
        }
    }
    Ok(())
}
