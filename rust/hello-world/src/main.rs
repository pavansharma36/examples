fn main() {
    let m = vec![1, 2, 3, 4, 5];
    let n = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

    m.iter().for_each(|&x| print!("{} ", x));
    n.into_iter().for_each(|x| println!("{}", x));

    println!("{:?}", n);
}
