package org.apache.spark.examples

import java.nio.file.{Files, Paths}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Column, DataFrame, Dataset, ForeachWriter, Row, SparkSession}
import org.apache.spark.sql.avro._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{IntegerType, StringType, StructType}
import com.datastax.spark.connector._
import org.apache.spark.examples.sparkSession.ConnectSession.getSparkSession
import org.apache.spark.sql.cassandra._

object app {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
      .setAppName("Connecting to Spark")
      .set("spark.dynamicAllocation.enabled","true")
      .set("spark.dynamicAllocation.testing","true")
      .set("spark.streaming.driver.writeAheadLog.closeFileAfterWrite","true")
      .set("spark.streaming.receiver.writeAheadLog.closeFileAfterWrite","true")

      .set("spark.cassandra.auth.username","cluster1-superuser")
      .set("spark.cassandra.auth.password","")

    val spark = SparkSession.builder().appName("cassandratest").config(conf).master("local").getOrCreate()

    import spark.implicits._
  /*  spark.sparkContext.setLogLevel("ERROR")

    val Data = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("/home/tech/emp.csv")
    Data.show()

    Data.select(to_avro(struct("*")) as "value")
      .write
      .format("kafka")
      .option("kafka.bootstrap.servers", ":9094")
      .option("kafka.request.required.acks", "1")
      .option("topic", "topic1-emp")
      .save()

   val Data1 = spark.read.format("csv").option("header", "true").option("inferSchema", "true").load("/home/tech/dept.csv")

    Data1.show()
    Data1.select(to_avro(struct("*")) as "value")
      .write
      .format("kafka")
      .option("kafka.bootstrap.servers", ":9094")
      .option("kafka.request.required.acks", "1")
      .option("topic", "topic1-dept")
      .save()

    /*Data.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map( "table" -> "emp", "keyspace" -> "mydb"))
      .option("ttl", "1000") //Use 1000 as the TTL
      .mode("APPEND")
      .save() */

   */

    val df = spark.readStream.format("kafka").option("kafka.bootstrap.servers", "9092").option("subscribe", "postgres.mydb.emp").option("multiLine", true).option("startingOffsets", "latest").load()


   df.select(col("topic").cast("String"), col("value").cast("String")).writeStream.foreachBatch {  (df: Dataset[Row], _: Long) => df.show() }.start().awaitTermination()

  }
}
