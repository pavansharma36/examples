use mini_redis::{client, Result};

#[tokio::main]
async fn main() -> Result<()> {
    let mut client = client::connect("127.0.0.1:8080").await?;

    // client.set("foo", "bar".into()).await?;

    let result = client.get("foo").await?;

    if let Some(res) = result {
        println!("Got value from the server: {:?}", String::from_utf8_lossy(res.as_ref()));
    } else {
        println!("Got None value");
    }

    Ok(())
}
