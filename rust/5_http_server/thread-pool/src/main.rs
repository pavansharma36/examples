use std::net::{TcpListener, TcpStream};
use std::thread;
use std::io::{Read, Write};
use std::fs;
use std::sync::{mpsc, Arc, Mutex};
use std::sync::mpsc::Receiver;
use std::time::Duration;

type Job = Box<dyn FnOnce() + Send + 'static>;

struct Worker {
    id: usize,
    thread: thread::JoinHandle<()>,
}

impl Worker {
    fn new(id: usize, receiver: Arc<Mutex<Receiver<Job>>>) -> Worker {
        let thread = thread::spawn(move || loop {
            let job = receiver.lock().unwrap().recv().unwrap();
            println!("Worker {} got a job; executing.", id);
            job();
        });
        Worker { id, thread }
    }
}

struct ThreadPool {
    workers: Vec<Worker>,
    sender: mpsc::Sender<Job>,
}

impl ThreadPool {
    fn new(thread_count: usize) -> ThreadPool {
        assert!(thread_count > 0);

        let (sender, receiver) = mpsc::channel();
        let receiver = Arc::new(Mutex::new(receiver));
        let mut workers = Vec::with_capacity(thread_count as usize);
        for i in 0..thread_count {
            workers.push(Worker::new(i, Arc::clone(&receiver)));
        }

        ThreadPool { workers, sender }
    }

    fn execute<F: FnOnce() + Send + 'static>(&self, f: F) {
        let job = Box::new(f);

        self.sender.send(job).unwrap();
    }
}

fn main() {
    let listener = TcpListener::bind(("127.0.0.1", 8080)).unwrap();
    let pool = ThreadPool::new(4);

    for stream in listener.incoming() {
        let stream = stream.unwrap();
        println!("Received a new connection");

        pool.execute(|| {
            handle_connection(stream);
        });
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
            thread::sleep(Duration::from_secs(30));
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
