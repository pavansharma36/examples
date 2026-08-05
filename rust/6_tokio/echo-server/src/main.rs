use tokio::io::{AsyncReadExt, AsyncWriteExt};
use tokio::net::{TcpListener, TcpStream};

#[tokio::main]
async fn main() -> std::io::Result<()> {
    let listener = TcpListener::bind("127.0.0.1:8080").await?;

    loop {
        let (socket, addr) = listener.accept().await?;
        println!("accepted connection from {:?}", addr);

        tokio::spawn(async {
            echo_server(socket).await;
        });
    }
}

async fn echo_server(mut socket: TcpStream) {
    let mut buf = vec![0; 1024];

    loop {
        match socket.read(&mut buf).await {
            // Return value of `Ok(0)` signifies that the remote has
            // closed
            Ok(0) => {
                println!("Echo server closed connection");
                return;
            },
            Ok(n) => {
                // Copy the data back to socket
                let resp = String::from_utf8_lossy(&buf[..n]).to_ascii_uppercase();
                if socket.write_all(resp.as_bytes()).await.is_err() {
                    // Unexpected socket error. There isn't much we can
                    // do here so just stop processing.
                    return;
                }
            }
            Err(_) => {
                // Unexpected socket error. There isn't much we can do
                // here so just stop processing.
                println!("Error while reading from socket");
                return;
            }
        }
    }
}