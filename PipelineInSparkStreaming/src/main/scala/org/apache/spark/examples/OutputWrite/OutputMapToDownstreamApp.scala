package org.apache.spark.examples.OutputWrite

import java.nio.file.{Files, Paths}

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.spark.examples.SourceConfiguration.Configurations
import org.apache.spark.examples.Utils.readFileFromResource
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.examples.sparkSession.ConnectSession.{getMysql, getSparkSession}
import org.apache.spark.sql.avro.from_avro
import org.apache.spark.sql.functions.col
import java.util.concurrent.Executors

import scala.collection.mutable.ListBuffer

object OutputMapToDownstreamApp {

  val spark: SparkSession = getSparkSession
  val mysql:(String,String,String) =getMysql
  var listofJobs=new ListBuffer[String]()
  val a = 0

  // Set number of threads via a configuration property
  val pool = Executors.newFixedThreadPool(5)
  implicit val xc = ExecutionContext.fromExecutorService(pool)

  def manOf[T: Manifest](t: T): Manifest[T] = manifest[T]

  def writeKafkaToMultipleApp(topicName : ListBuffer[String], schemaName: ListBuffer[String], KafkaIP:String,config: Configurations) = {


    val topics=topicName.mkString(",")

    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", s"$KafkaIP:9094")
      .option("subscribe", s"$topics")
      .option("startingOffsets", "latest") //latest
      .load()

    df.select(col("topic"), col("value")).writeStream
      .foreachBatch {
        (df: Dataset[Row], _: Long) =>
          //df.persist()
          val tpc=df.select("topic").distinct().collect().map(_.getString(0)).mkString(" ")

          if (!tpc.isEmpty) {
            val getPartialTopicName = tpc.split("-")(1)
            println(getPartialTopicName)
            if(schemaName.contains(s"$getPartialTopicName.avsc")) {

              val avroFormatSchema = new String(
                Files.readAllBytes(Paths.get(readFileFromResource.readFromResource(s"/schema/$getPartialTopicName.avsc").getAbsolutePath)))

              val topicDF = df.filter(col("topic") === tpc).select(from_avro(col("value"), avroFormatSchema).as("data"))
                .select("data.*")


              for(a <- 0 until 1 ){
              {
                val thread = new Thread {
                  override def run: Unit = {
                    if (!(config.outputOptions.apply("DB") == null)) org.apache.spark.examples.OutputWrite.WriteToMySQL.MySqlWrite(topicDF, getPartialTopicName, mysql._1, mysql._2, mysql._3)
                    if (!(config.outputOptions.apply("NoSqlDb") == null)) org.apache.spark.examples.OutputWrite.WriteToCassanDra.CassandraWrite(topicDF, getPartialTopicName)
                    if (!(config.outputOptions.apply("FileLoc") == null)) org.apache.spark.examples.OutputWrite.WriteToFile.WriteAsParquet(topicDF, getPartialTopicName,config.outputOptions.apply("FileLoc"))
                  }

                  }
                thread.start()
                }
              }


            }
            // df.unpersist()
          }
      }.start().awaitTermination()



  }

}
