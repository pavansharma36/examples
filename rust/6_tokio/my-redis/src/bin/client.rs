use bytes::Bytes;
use mini_redis::client;
use tokio::sync::{mpsc, oneshot};

type Responder<T> = oneshot::Sender<mini_redis::Result<T>>;


#[derive(Debug)]
enum Command {
    Get {
        key: String,
        resp: Responder<Option<Bytes>>,
    },
    Set {
        key: String,
        val: Bytes,
        resp: Responder<()>,
    }
}

#[tokio::main]
async fn main() {

    let (tx, mut rx) = mpsc::channel(32);

    let tx2 = tx.clone();

    let t1 = tokio::spawn(async move {
        let (resp_tx, resp_rx) = oneshot::channel();
        tx.send(Command::Get {
            key: "say".to_string(),
            resp: resp_tx,
        }).await.unwrap();

        let resp = resp_rx.await.unwrap();
        println!("Got : {:?}", resp);

    });

    let t2 = tokio::spawn(async move {
        let (resp_tx, resp_rx) = oneshot::channel();
        tx2.send(Command::Set {
            key: "foo".to_string(),
            val: "bar".into(),
            resp: resp_tx,
        }).await.unwrap();

        let resp = resp_rx.await.unwrap();
        println!("Got : {:?}", resp);
    });

    let manager = tokio::spawn(async move {
        let mut client = client::connect("127.0.0.1:8080").await.unwrap();

        while let Some(command) = rx.recv().await {

            match command {
                Command::Get { key, resp } => {
                    let res = client.get(&key).await;
                    resp.send(res).unwrap();
                },
                Command::Set { key, val, resp } => {
                    let res =  client.set(&key, val).await;
                    resp.send(res).unwrap();
                },
            }
        }
    });

    t1.await.unwrap();
    t2.await.unwrap();

    manager.await.unwrap();


}