use std::collections::HashMap;
use std::fmt::{Display, Formatter};
use std::io::{Error, Read, Write};
use std::net::{TcpListener, TcpStream};
use std::sync::mpsc::Sender;
use std::sync::mpsc::{Receiver, SendError};
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
    username: Option<String>,
}

enum Message {
    ClientConnected(Client),
    ClientDisconnected(Client),
    NewMessage(Client, Vec<u8>),
}

fn handle_client(
    stream: Arc<TcpStream>,
    message_sender: Sender<Message>,
) -> Result<(), SendError<Message>> {
    // Keep the stream in scope by looping indefinitely within the handler
    let username = petname::petname(3, "-").unwrap_or("anonymous".to_string());
    let _ = stream.as_ref().write(
        format!(
            "Welcome to chatbot impl in Rust 🦀!!! Your username is {}\n",
            username
        )
        .as_bytes(),
    );
    message_sender.send(Message::ClientConnected(Client {
        conn: stream.clone(),
        username: Some(username),
    }))?;

    let ctrl_c: Vec<u8> = vec![255, 244, 255, 253, 6];
    loop {
        let mut buffer: Vec<u8> = vec![0; 128];
        // Read data from the stream
        match stream.as_ref().read(&mut buffer) {
            Ok(0) => {
                // Connection closed by client
                println!("Client disconnected.");
                message_sender.send(Message::ClientDisconnected(Client {
                    conn: stream.clone(),
                    username: None,
                }))?;
                return Ok(());
            }
            Ok(n) => {
                // Data received, process it and potentially write a response

                // Echo the data back to the client as an example
                let data = buffer[..n].to_vec();
                if data == ctrl_c {
                    println!("Client pressed Ctrl+C.");
                    message_sender.send(Message::ClientDisconnected(Client {
                        conn: stream.clone(),
                        username: None,
                    }))?;
                    return Ok(());
                }
                message_sender.send(Message::NewMessage(
                    Client {
                        conn: stream.clone(),
                        username: None,
                    },
                    data,
                ))?;
            }
            Err(e) => {
                eprintln!("Error reading from stream: {}", e);
                message_sender.send(Message::ClientDisconnected(Client {
                    conn: stream.clone(),
                    username: None,
                }))?;
                return Ok(());
            }
        }
    }
    // The stream goes out of scope here and is closed automatically.
}

fn handle_server(receiver: Receiver<Message>) -> Result<(), Error> {
    let mut clients = HashMap::new();
    loop {
        match receiver.recv() {
            Ok(message) => match message {
                Message::ClientConnected(client) => {
                    println!(
                        "Client connected: {} : {}",
                        client.username.clone().unwrap_or(String::from("anonymous")),
                        Sensitive::new(client.conn.peer_addr()?)
                    );
                    clients.insert(client.conn.clone().peer_addr()?, client);
                }
                Message::ClientDisconnected(client) => {
                    println!(
                        "Client disconnected: {}",
                        Sensitive::new(client.conn.peer_addr()?)
                    );
                    clients.remove(&client.conn.peer_addr()?);
                }
                Message::NewMessage(client, message) => {
                    let username = clients
                        .get(&client.conn.peer_addr()?)
                        .map(|c| c.username.clone().unwrap())
                        .unwrap_or(String::from("anonymous"));
                    if !*SAFE_MODE {
                        print!(
                            "Received message from {}: {}",
                            username,
                            String::from_utf8_lossy(&message)
                        );
                    }
                    for (addr, c) in clients.iter() {
                        if client.conn.peer_addr()? != *addr {
                            let _ = c.conn.as_ref().write(format!("{}: ", username).as_bytes());
                            let _ = c.conn.as_ref().write(&message);
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
                let client_sender = message_sender.clone();
                thread::spawn(|| {
                    let _ = handle_client(Arc::new(stream), client_sender).map_err(|err| {
                        eprintln!("Error handling client: {}", err);
                    });
                });
            }
            Err(e) => println!("{:?}", e),
        }
    }
    Ok(())
}
