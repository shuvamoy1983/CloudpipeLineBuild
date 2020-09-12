package org.apache.spark.examples.OutputWrite

import org.apache.spark.examples.SourceConfiguration.Configurations
import org.apache.spark.examples.schmaJson.JsonStructures.{Schema, getEmpSchema}
import org.apache.spark.sql.avro.to_avro
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.sql.functions.{col, from_json, struct}
import org.apache.spark.sql.types.{IntegerType, StringType, StructType}

import scala.collection.mutable.ListBuffer

object PostgresToKafka {

  def manOf[T: Manifest](t: T): Manifest[T] = manifest[T]

  def writePostgresToKafka(spark: SparkSession, OrgtopicName: ListBuffer[String],
                           postgresTopic: ListBuffer[String],
                           KafkaIP: String,
                           config: Configurations,
                           kafkaSourceeIP: String) = {

    val topics = postgresTopic.mkString(",")
    val len = OrgtopicName.length
    var a=0
    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", s"$kafkaSourceeIP:19092")
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
        println("mre", tpc)

        //println(tpc.split("\\.")(1))
        if (!tpc.isEmpty) {

          val getPartialTopicNameFromostgres = tpc.split("\\.")(2)
          val getPartialOriginalTopicName = OrgtopicName(0).split("-")(1)

          println(s"val $getPartialOriginalTopicName and $getPartialTopicNameFromostgres")
            val schema = Schema(getPartialTopicNameFromostgres)
            println(schema)

           val DF = df.select(from_json(col("value"), schema).as("data"))
             .select("data.*")

            DF.select(col("payload.after.*")).show()

             DF.select(to_avro(struct(col("payload.after.*"))) as "value")
            .write
            .format("kafka")
            .option("kafka.bootstrap.servers", s"$KafkaIP:9094")
            .option("kafka.request.required.acks", "1")
            .option("topic", s"topic1-$getPartialTopicNameFromostgres")
            .save()

          // df.unpersist()
        }
    }.start().awaitTermination()
  }
}
