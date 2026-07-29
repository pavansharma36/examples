use std::{fs, thread};
use std::io::Write;
use std::net::{TcpListener, TcpStream};
use std::io::{ Read };
use std::time::Duration;

fn main() {
    let listener = TcpListener::bind("127.0.0.1:8080").unwrap();

    for stream in listener.incoming() {
        let stream = stream.unwrap();
        println!("Received a new connection");

        handle_connection(stream);
    }
}

fn handle_connection(mut stream: TcpStream) {
    let mut buffer = [0; 1024];
    stream.read(&mut buffer).unwrap();

    let get = b"GET / HTTP/1.1\r\n";

    let (status_line, filename);
    if let Some(first_line_bytes) = buffer.split(|&b| b == b'\n').next() {
        (status_line, filename) = if buffer.starts_with(get) {
            println!("Sending 200 OK for root request");
            ("HTTP/1.1 200 OK", "hello.html")
        } else {
            println!("Sending 404 OK for non root request {} with delay", String::from_utf8_lossy(first_line_bytes));
            thread::sleep(Duration::from_secs(5));
            ("HTTP/1.1 404 NOT FOUND", "404.html")
        };
    } else {
        println!("Sending 404 OK for non http request");
        (status_line, filename) = ("HTTP/1.1 404 NOT FOUND", "404.html")
    }

    let contents = fs::read_to_string(filename).unwrap();
    let response = format!(
        "{}\r\nContent-Length: {}\r\n\r\n{}",
        status_line,
        contents.len(),
        contents
    );
    stream.write(response.as_bytes()).unwrap();
    stream.flush().unwrap();
}