
use futures::executor::block_on;

async fn count(n: i32) {
    for _ in 0..n {
        println!("count: {}", n);
    }
}

async fn count_to(n: i32) {
    count(n).await;
}

fn main() {
    block_on(count_to(100));
}
