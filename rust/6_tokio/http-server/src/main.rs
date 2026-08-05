use tokio::io::{AsyncReadExt, AsyncWriteExt};
use tokio::net::TcpListener;
// use tokio::sync::mpsc;
//
// async fn ping_handler(mut input: mpsc::Receiver<()>) {
//     let mut count: usize = 0;
//     while let Some(_) = input.recv().await {
//         count += 1;
//         println!("Received {count} pings so far.");
//     }
//     println!("ping_handler complete");
// }
//
// #[tokio::main]
// async fn main() {
//     let (sender, receiver) = mpsc::channel(32);
//     let ping_handler_task = tokio::spawn(ping_handler(receiver));
//     for i in 0..10 {
//         sender.send(()).await.expect("Failed to send ping.");
//         println!("Sent {} pings so far.", i + 1);
//     }
//     drop(sender);
//     ping_handler_task.await.expect("Something went wrong in ping handler task.");
// }

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let listener = TcpListener::bind(("0.0.0.0", 8080)).await?;

    loop {
        let (mut socket, addr) = listener.accept().await?;
        println!("Accepted connection from: {}", addr);

        tokio::spawn(async move {
            socket.write_all(b"Who are you?\n").await.unwrap();

            let mut buff = [0; 1024];
            let name_size = socket.read(&mut buff).await.unwrap();
            let name = std::str::from_utf8(&buff[..name_size]).unwrap().trim();
            let reply = format!("Hello, {}!\n", name);
            socket.write_all(reply.as_bytes()).await.unwrap();
        });
    }
}
