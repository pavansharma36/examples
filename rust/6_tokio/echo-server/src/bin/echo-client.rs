use std::io::Error;
use tokio::io;
use tokio::io::{AsyncReadExt, AsyncWriteExt};
use tokio::net::TcpStream;

#[tokio::main]
async fn main() -> Result<(), Error> {
    let socket = TcpStream::connect("127.0.0.1:8080").await?;
    let (mut rd, mut wr) = io::split(socket);


    tokio::spawn(async move {
        wr.write_all(b"hello\r\n").await?;
        wr.write_all(b"world\r\n").await?;

        wr.shutdown().await?;

        Ok::<(), std::io::Error>(())
    });

    let mut buff = vec![0; 128];
    loop {
        let n = rd.read(&mut buff).await?;
        if n == 0 {
            break;
        }

        println!("Got {}", String::from_utf8_lossy(&buff[..n]));
    }

    Ok(())
}