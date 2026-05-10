use std::{env, process};
use minigrep::{run, Config};

fn main() {
    let args: Vec<String> = env::args().collect();

    let config: Config = Config::new(&*args).unwrap_or_else(|err| {
        eprintln!("Error: {}", err);
        process::exit(1);
    });

    println!("Searching for {}", config.query);
    println!("In file {}", config.filename);
    if let Err(e) = run(config) {
        println!("Application error: {}", e);
        process::exit(1);
    }
}

