use std::collections::HashMap;
use std::io::Error;
use std::sync::{Arc, Mutex};
use bytes::Bytes;
use mini_redis::{Command, Connection, Frame};
use tokio::net::{TcpListener, TcpStream};

const DEFAULT_PORT: &str = "7000";

type Db = Arc<Mutex<HashMap<String, Bytes>>>;

#[tokio::main]
async fn main() -> Result<(), Error> {

    let listener = TcpListener::bind(format!("0.0.0.0:{}", DEFAULT_PORT)).await?;
    let db: Db = Arc::new(Mutex::new(HashMap::new()));

    loop {
        let (socket, addr) = listener.accept().await?;
        println!("accepted connection from {}", addr);
        let db = db.clone();
        tokio::spawn(async move {
            process(socket, db).await?;
            Ok::<(), mini_redis::Error>(())
        });
    }

}

async fn process(stream: TcpStream, db: Db) -> mini_redis::Result<()> {

    let mut connection = Connection::new(stream);

    while let Some(frame) = connection.read_frame().await? {
        let response = match Command::from_frame(frame)? {
            Command::Get(cmd) => {
                if let Some(val) = db.lock().unwrap().get(cmd.key()) {
                    println!("Get command: {}={}", cmd.key(), String::from_utf8_lossy(val));
                    Frame::Bulk(val.clone())
                } else {
                    println!("Get command: {}=Nil", cmd.key());
                    Frame::Null
                }
            },
            Command::Set(cmd) => {
                println!("Set command: {:?}", cmd);
                db.lock().unwrap().insert(cmd.key().to_string(), cmd.value().clone());
                Frame::Simple("OK".into())
            }
            _ => todo!(),
        };
        connection.write_frame(&response).await?;
    }

    Ok(())

}