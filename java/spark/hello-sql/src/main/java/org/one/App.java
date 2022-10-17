package org.one;

import javax.xml.crypto.Data;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

import static org.apache.spark.sql.functions.*;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        SparkSession spark = SparkSession.builder()
            .appName("test")
            .config("spark.master", "local[5]")
            .getOrCreate();

        Dataset<Row> movies = movies(spark);

        Dataset<Row> ratings = ratings(spark);

        Dataset<Row> movieRatingCountDesc = ratings.groupBy("movieId")
                .avg("rating");
        movieRatingCountDesc.join(movies, movies.col("movieId").equalTo(movieRatingCountDesc.col("movieId")))
                .sort(desc("avg(rating)"))
                    .show(100);


        //movieRatingCountDesc.show();

//        movieRatingCountDesc.join(movies, movieRatingCountDesc.col("movieId")
//            .equalTo(movies.col("movieId"))).sort(desc("count")).show();

//        movies.createOrReplaceTempView("movies");
//        ratings.createOrReplaceTempView("ratings");
//
//        Dataset<Row> movieRatingDesc = spark.sqlContext().sql("SELECT m.movieId, m.title, r.ratingCount FROM movies m " +
//            "INNER JOIN (SELECT movieId, count(movieId) AS ratingCount FROM ratings GROUP BY movieId) r " +
//            "ON m.movieId = r.movieId " +
//            "ORDER BY r.ratingCount DESC");
//
//        movieRatingDesc.write()
//            .option("header", "true")
//            .csv("/home/pavan/Desktop/test_spark.csv");

        System.out.println(spark);
    }

    private static Dataset<Row> movies(SparkSession spark) {
        StructType st = new StructType()
            .add("movieId", "integer")
            .add("title", "string")
            .add("genres", "string");

        return spark.read().option("header", "true")
            .schema(st)
            .csv("/home/pavan/Desktop/data/ml-25m/movies.csv");
    }

    private static Dataset<Row> ratings(SparkSession spark) {
        StructType st = new StructType()
            .add("userId", "integer")
            .add("movieId", "integer")
            .add("rating", "float")
            .add("timestamp", "long");

        return spark.read().option("header", "true")
            .schema(st)
            .csv("/home/pavan/Desktop/data/ml-25m/ratings.csv");
    }


//    StructType st = new StructType()
//        .add("id", "integer")
//        .add("firstname", "string")
//        .add("lastname", "string")
//        .add("email", "string")
//        .add("profession", "string");
//
//    Dataset<Row> df = spark.read().option("header", "true")
//        .schema(st)
//        .csv("/home/pavan/Desktop/test.csv");
//        df.show(10);
//
//        df.createOrReplaceTempView("test_view");
//
//        df.sqlContext().sql("select * from test_view where profession = 'developer'")
//                .show();
//
//        spark.sqlContext().sql("select * from test_view where profession = 'developer'")
//                .show();
//        df.groupBy("profession").count().show();
}
