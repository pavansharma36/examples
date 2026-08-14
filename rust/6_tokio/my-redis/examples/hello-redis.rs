use mini_redis::{Result, client};

const DEFAULT_PORT: &str = "7000";

#[tokio::main]
async fn main() -> Result<()> {

    let mut client = client::connect(format!("127.0.0.1:{}", DEFAULT_PORT)).await?;

    client.set("hello", "world".into()).await?;

    let result = client.get("hello").await?;

    println!("got value: {:?}", result);

    Ok(())
}