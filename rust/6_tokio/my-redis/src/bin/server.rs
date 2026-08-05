use std::collections::HashMap;
use std::io::Error;
use std::sync::{Arc, Mutex};
use bytes::Bytes;
use tokio::net::{TcpListener, TcpStream};
use mini_redis::{Command, Connection, Frame};

#[tokio::main]
async fn main() -> Result<(), Error>{
    let listener = TcpListener::bind("127.0.0.1:8080").await?;

    let db = Arc::new(Mutex::new(HashMap::new()));

    loop {
        let (stream, addr) = listener.accept().await?;
        println!("new connection: {}", addr);

        let db = Arc::clone(&db);

        tokio::spawn(async move {
            process(stream, db).await;
        });
    }
}

async fn process(stream: TcpStream, db: Arc<Mutex<HashMap<String, Bytes>>>) {
    let mut connection = Connection::new(stream);

    while let Some(frame) = connection.read_frame().await.unwrap() {
        let response = match Command::from_frame(frame).unwrap() {
            Command::Set(cmd) => {
                db.lock().unwrap().insert(cmd.key().to_string(), cmd.value().clone());
                Frame::Simple("OK".to_string())
            },
            Command::Get(cmd) => {
                if let Some(val) = db.lock().unwrap().get(&cmd.key().to_string()) {
                    Frame::Bulk(val.clone().into())
                } else {
                    Frame::Null
                }
            }
            _ => todo!(),
        };

        connection.write_frame(&response).await.unwrap();
    }
}