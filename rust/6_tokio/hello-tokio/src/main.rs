async fn count_with_sleep(i: u8) {
    for j in 0..i {
        println!("Current count: {}", j);
        tokio::time::sleep(std::time::Duration::from_secs(1)).await;
    }
}

#[tokio::main]
async fn main() {
    let async_task = tokio::spawn(count_with_sleep(10));

    for i in 0..5 {
        println!("Main task: {i}");
        tokio::time::sleep(std::time::Duration::from_secs(1)).await;
    }

    async_task.await.unwrap();
}
