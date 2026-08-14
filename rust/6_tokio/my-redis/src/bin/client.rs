use bytes::Bytes;
use mini_redis::{Result, client};
use tokio::sync::{mpsc};
use tokio::sync::oneshot::{Sender, channel};

const DEFAULT_PORT: u16 = 7000;

enum Command {
    Get { key: String, resp: Sender<Result<Option<Bytes>>> },
    Set { key: String, value: Bytes, resp: Sender<Result<()>> },
}

#[tokio::main]
async fn main() -> Result<()> {
    let mut client = client::connect(format!("127.0.0.1:{}", DEFAULT_PORT)).await?;

    let (tx, mut rx) = mpsc::channel(32);

    let tx2 = tx.clone();
    let t1 = tokio::spawn(async move {
        let (sender, receiver) = channel();
        tx.send(Command::Get {
            key: "foo".to_string(),
            resp: sender,

        })
        .await
        .unwrap();

        let resp = receiver.await.unwrap();
        println!("GOT: {:?}", resp);

    });

    let t2 = tokio::spawn(async move {
        let (sender, receiver) = channel();
        tx2.send(Command::Set {
            key: "foo".to_string(),
            value: "bar".into(),
            resp: sender,
        })
        .await
        .unwrap();

        let res = receiver.await.unwrap();
        println!("GOT = {:?}", res);
    });

    while let Some(command) = rx.recv().await {
        match command {
            Command::Get { key, resp } => {
                println!("get {}", key);
                let res =client.get(&key).await;
                resp.send(res).unwrap();
            },
            Command::Set { key, value, resp } => {
                println!("set {}", key);
                let res = client.set(&key, value).await;
                resp.send(res).unwrap();
            }
        }
    }

    t1.await?;
    t2.await?;

    Ok(())
}
