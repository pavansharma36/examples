use anyhow::Context;
use axum::http::StatusCode;
use axum::routing::get;
use axum::{Json, Router};
use axum::response::IntoResponse;
use serde::Serialize;
use tokio::net::TcpListener;

#[tokio::main]
async fn main() -> anyhow::Result<()> {
    let app: Router = Router::new().route("/", get(hello_json))
        .layer(tower_http::catch_panic::CatchPanicLayer::new());

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

struct AppError(anyhow::Error);

impl From<anyhow::Error> for AppError {
    fn from(err: anyhow::Error) -> Self {
        Self(err)
    }
}

impl IntoResponse for AppError {
    fn into_response(self) -> axum::response::Response {
        (StatusCode::INTERNAL_SERVER_ERROR, self.0.to_string()).into_response()
    }
}

async fn hello_json() -> Result<(StatusCode, Json<Response>), AppError> {
    let response = Response {
        message: get_message().context("failed to get message")?,
    };
    Ok((StatusCode::OK, Json(response)))
}

fn get_message() -> anyhow::Result<&'static str> {
    if rand::random() {
        anyhow::bail!("No message for you");
    }
    Ok("Hello, World!")
}
