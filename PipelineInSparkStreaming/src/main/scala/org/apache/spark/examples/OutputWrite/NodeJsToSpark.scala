package org.apache.spark.examples.OutputWrite

import org.apache.spark.examples.SourceConfiguration.Configurations
import org.apache.spark.examples.schmaJson.JsonStructuesForNodeJs.SchemaForNodeJs
import org.apache.spark.sql.avro.to_avro
import org.apache.spark.sql.functions.{col, from_json, struct}
import org.apache.spark.sql.{Dataset, Row, SparkSession}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}

object NodeJsToSpark {


  def writeNodeJsToKafka(spark: SparkSession,
                         OrgtopicName: ListBuffer[String],
                         NodeJsTopic: ListBuffer[String],
                         KafkaIP: String,
                         config: Configurations) = {

    val topics = NodeJsTopic.mkString(",")
    val len = OrgtopicName.length
    var a = 0
    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", s"$KafkaIP:9094")
      .option("subscribe", s"$topics")
      .option("multiLine", true)
      .option("startingOffsets", "latest") //latest
      .load()

    //val StringDF = df.selectExpr("CAST(value AS STRING)","CAST(topic AS STRING)")
    //StringDF.printSchema()


    df.select(col("topic").cast("String"), col("value").cast("String")).writeStream.foreachBatch {
      (df: Dataset[Row], _: Long) =>
        df.show()
        //df.persist()
        val tpc = df.select("topic").distinct().collect().map(_.getString(0)).mkString(" ")

        println(tpc)

        //println(tpc.split("\\.")(1))
        if (!tpc.isEmpty) {

          val getPartialTopicNameFromNodeJS = tpc.split("\\.")(2)

          println(s"val  $getPartialTopicNameFromNodeJS")
          val schema = SchemaForNodeJs(getPartialTopicNameFromNodeJS)
          println(schema)

          val DF = df.select(from_json(col("value"), schema).as("data"))
            .select("data.*")

          DF.select(col("*")).show()

          DF.select(to_avro(struct(col("*"))) as "value")
            .write
            .format("kafka")
            .option("kafka.bootstrap.servers", s"$KafkaIP:9094")
            .option("kafka.request.required.acks", "1")
            .option("topic", s"topic1-$getPartialTopicNameFromNodeJS")
            .save()

          // df.unpersist()
        }
    }.start().awaitTermination()
  }

}
