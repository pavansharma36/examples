use std::rc::Rc;
use std::thread;
use std::sync::{mpsc, Arc, Mutex};
use std::time::Duration;

fn multi_threaded_count() {
    let counter = Arc::new(Mutex::new(0));
    let mut handles = vec![];
    for _ in 0..10 {
        let counter = Arc::clone(&counter);
        let handle = thread::spawn(move || {
            let mut num = counter.lock().unwrap();
            *num += 1;
        });
        handles.push(handle);
    }
    for handle in handles {
        handle.join().unwrap();
    }
    println!("Result: {}", *counter.lock().unwrap());
}

fn scoped_threads() {
    let mut s = String::from("hello");
    thread::scope(|scope| {
        scope.spawn(|| {
            println!("scoped {}", s.capacity());
        });
    });

    thread::scope(|scope| {
        scope.spawn(|| {
            println!("Another thread {}", s.capacity());
            s.push_str(" world");
            println!("{}", s.capacity());
        });
    });

    thread::scope(|scope| {
        scope.spawn(|| {
            println!("Another thread {}", s.capacity());
            s.push_str("!!!");
            println!("{}", s.capacity());
        });
    });
}

fn main() {
    scoped_threads();
}
