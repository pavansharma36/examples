use std::sync::mpsc;
use actix_web::web::Data;
use actix_web::{App, HttpResponse, HttpServer, Responder, web};
use one_mrc::models::{Event, Stats};
use one_mrc::state::{AppState, start_event_handler};

#[tokio::main]
async fn main() {
    let (tx, rx) = mpsc::channel();

    start_event_handler(rx);

    let app_state = AppState::new(tx);

    let app_data = Data::new(app_state.clone());

    HttpServer::new(move || {
        App::new()
            .app_data(app_data.clone())
            .route("/health", web::get().to(health))
            .route("/event", web::post().to(event))
            .route("/stats", web::get().to(stats))
    })
    .bind("0.0.0.0:8080")
    .unwrap()
    .workers(12)
    .run()
    .await
    .unwrap();
}

async fn health() -> impl Responder {
    HttpResponse::Ok().finish()
}

async fn event(event: web::Json<Event>, state: Data<AppState>) -> impl Responder {
    state.send(event.clone());
    HttpResponse::Ok().finish()
}

async fn stats(state: Data<AppState>) -> impl Responder {
    let (tx, rx) = tokio::sync::oneshot::channel::<Stats>();
    state.stats(tx);
    println!("Waiting for stats");
    let res = rx.await.unwrap_or_else(|_| Stats::default());
    println!("Received stats: {:?}", res);
    HttpResponse::Ok().json(res)
}
