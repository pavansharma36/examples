use opentelemetry::global;
use opentelemetry::trace::TracerProvider;
use opentelemetry_otlp::WithExportConfig;
use opentelemetry_sdk::Resource;
use opentelemetry_sdk::trace::SdkTracerProvider;
use tracing::{info, instrument};
use tracing_subscriber::{layer::SubscriberExt, util::SubscriberInitExt};

fn init_tracing() -> Result<SdkTracerProvider, Box<dyn std::error::Error>> {
    let exporter = opentelemetry_otlp::SpanExporter::builder()
        .with_tonic()
        .with_endpoint("http://localhost:4317")
        .build()?;

    let resource = Resource::builder()
        .with_service_name("hello-tracing")
        .build();

    let provider = SdkTracerProvider::builder()
        .with_batch_exporter(exporter)
        .with_resource(resource)
        .build();

    let tracer = provider.tracer("jaeger-demo");

    tracing_subscriber::registry()
        .with(tracing_subscriber::EnvFilter::new("info"))
        .with(tracing_subscriber::fmt::layer())
        .with(tracing_opentelemetry::layer().with_tracer(tracer))
        .init();

    global::set_tracer_provider(provider.clone());

    Ok(provider)
}

#[instrument]
async fn do_some_work() {
    info!("starting work");

    tokio::time::sleep(std::time::Duration::from_millis(100)).await;

    nested_work().await;

    info!("finished work");
}

#[instrument]
async fn nested_work() {
    info!("doing nested work");

    tokio::time::sleep(std::time::Duration::from_millis(50)).await;
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let provider = init_tracing()?;

    info!("application started");

    do_some_work().await;

    info!("application shutting down");

    // Important: flush/export remaining spans before exiting.
    provider.shutdown()?;

    Ok(())
}