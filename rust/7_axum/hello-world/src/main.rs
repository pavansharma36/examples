use anyhow::Context;
use axum::http::StatusCode;
use axum::routing::get;
use axum::{Json, Router};
use serde::Serialize;
use tokio::net::TcpListener;

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    let app: Router = Router::new().route("/", get(hello_json));

    let listener = TcpListener::bind("127.0.0.1:3000")
        .await
        .context("failed to bind TCP Listener")?;

    axum::serve(listener, app)
        .await
        .context("error serving axum api")?;

    Ok(())
}

#[derive(Serialize)]
struct Response {
    message: &'static str,
}

async fn hello_json() -> (StatusCode, Json<Response>) {
    (
        StatusCode::OK,
        Json(Response {
            message: "Hello World!",
        }),
    )
}
