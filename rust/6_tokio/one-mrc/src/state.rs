use crate::models;
use crate::models::{Event, Message};
use std::collections::HashSet;
use tokio::sync::mpsc::{Receiver, Sender};

#[derive(Clone)]
pub struct AppState {
    tx: Sender<Message>,
}

impl AppState {
    pub fn new(tx: Sender<Message>) -> AppState {
        AppState { tx }
    }

    pub async fn send(&self, event: Event) {
        self.tx.send(Message::Event(event)).await.unwrap();
    }

    pub async fn stats(&self, tx: tokio::sync::oneshot::Sender<models::Stats>) {
        self.tx.send(Message::Stats(tx)).await.unwrap();
    }
}

struct Stats {
    total_requests: u64,
    sum: f64,
    users: HashSet<String>,
}

pub async fn start_event_handler(mut rx: Receiver<Message>) {
    let mut stats = Stats {
        total_requests: 0,
        sum: 0.0,
        users: HashSet::new(),
    };
    while let Some(m) = rx.recv().await {
        match m {
            Message::Event(event) => {
                stats.total_requests += 1;
                stats.sum = stats.sum + event.value;
                stats.users.insert(event.user_id);
            },
            Message::Stats(stats_sender) => {
                println!("Stats {:?}", stats_sender);
                let stats: models::Stats = models::Stats::new(
                    stats.total_requests,
                    stats.users.len(),
                    stats.sum,
                    stats.sum / stats.total_requests as f64,
                );
                stats_sender.send(stats).unwrap();
                println!("Sent stats");
            },
        };
    };
    println!("Event Handler completed")
}
