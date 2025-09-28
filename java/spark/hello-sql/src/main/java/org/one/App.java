package org.one;

import java.util.HashMap;
import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

import static org.apache.spark.sql.functions.*;


/**
 * Hello world!
 *
 */
public class App {
  public static void main(String[] args) {
    SparkSession spark = SparkSession.builder().appName("test")
        .config("spark.sql.warehouse.dir", "/user/hive/warehouse").enableHiveSupport()
        .getOrCreate();


    spark.sql("SHOW DATABASES").show();

    spark.sql("SHOW TABLES FROM imdb").show();

    Dataset<Row> ratings = spark.table("imdb.rating");

    Dataset<Row> avgRatings = ratings.groupBy("movieId")
        .agg(avg("rating").as("avg_rating"), count("movieId").as("total_rating"));

    Dataset<Row> movies = spark.table("imdb.movie");

    avgRatings = avgRatings.join(movies, avgRatings.col("movieId").equalTo(movies.col("id")))
        .withColumn("avg_by_count", col("avg_rating").multiply(col("total_rating")).$div(lit(5)))
        .sort(col("avg_by_count").desc());

    String path = "/user/pavan/ml-25m/top_ratings";
    System.out.println("Writing to " + path);
    avgRatings.write()
        .mode("overwrite")
        .format("parquet")
        .option("path", path)
        .saveAsTable("imdb.top_ratings");

    System.out.println(spark);
  }


}
