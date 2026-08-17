use axum::extract::State;
use axum::routing::{get, post};
use axum::{Json, Router};
use one_mrc::models::{Event, Stats};
use one_mrc::state::{AppState, start_event_handler};
use serde_json::{Value, json};
use std::ops::Deref;
use std::sync::mpsc;

#[tokio::main]
async fn main() {
    let (tx, rx) = mpsc::channel();

    start_event_handler(rx);

    let app_state = AppState::new(tx.clone());

    let app = Router::new()
        .route("/health", get(|| async { "ok" }))
        .route("/event", post(event))
        .route("/stats", get(stats))
        .with_state(app_state);

    // run our app with hyper, listening globally on port 3000
    let listener = tokio::net::TcpListener::bind("0.0.0.0:8080").await.unwrap();
    axum::serve(listener, app).await.unwrap();
}

async fn event(state: State<AppState>,
               event: Json<Event>) -> Json<Value> {
    state.send(event.deref().clone());
    Json(json!({"result": "OK"}))
}

async fn stats(stat: State<AppState>) -> Json<Stats> {
    let (tx, rx) = tokio::sync::oneshot::channel::<Stats>();
    stat.stats(tx);
    println!("Waiting for stats");
    let res = rx.await.unwrap_or_else(|_| Stats::default());
    println!("Received stats: {:?}", res);
    Json(res)
}