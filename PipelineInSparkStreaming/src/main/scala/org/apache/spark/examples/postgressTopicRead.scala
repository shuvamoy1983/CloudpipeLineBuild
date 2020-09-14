package org.apache.spark.examples

import java.nio.file.{Files, Paths}
import java.util.concurrent.Executors
import java.util.regex.Pattern

import org.apache.spark.SparkConf
import org.apache.spark.examples.FileParsing.fileParse.parseYamlFile
import org.apache.spark.examples.OutputWrite.{NodeJsToSpark, PostgresToKafka}
import org.apache.spark.examples.SourceConfiguration.Configurations
import org.apache.spark.examples.Utils.readFileFromResource
import org.apache.spark.examples.allInputFiles.ListOfInputFile
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.examples.sparkSession.ConnectSession.getSparkSession
import org.apache.spark.sql.avro.from_avro
import org.apache.spark.sql.functions.{col, from_json, struct}
import org.apache.spark.sql.streaming.{DataStreamWriter, OutputMode}
import org.apache.spark.sql.types.{IntegerType, MapType, StringType, StructType}
import org.apache.spark.examples.schmaJson.JsonStructures._
import org.apache.spark.examples.sparkSession.ConnectSession

import scala.collection.mutable.ListBuffer


object postgressTopicRead {

  def main(args: Array[String]): Unit = {

    val ListOfFiles = ListOfInputFile.ListOfFilesToRead
      ListOfFiles.foreach({ resourcePath =>
      val tmpLoc = readFileFromResource.readFromInputResource(resourcePath)
      val config: Configurations = parseYamlFile(s"$tmpLoc")

        val kafkaDestIP=args(0)
        val cassandraHost=args(1)
        val cassandraUserName=args(2)
        val cassandraPassword=args(3)
        val MySqlHost=args(4)
        val MySqlUserName=args(5)
        val MySqlPassword=args(6)
        val kafkaSourceeIP=args(7)
        val ElasticSearchIP=Option(args(8)).getOrElse("")


      ConnectSession.init(config, MySqlHost, MySqlUserName, MySqlPassword, cassandraHost, cassandraUserName, cassandraPassword,ElasticSearchIP)
      val spark: SparkSession = getSparkSession

      val postgresTopic = new ListBuffer[String]()
      val mytopicList = new ListBuffer[String]()
      val nodejsTopicList= new ListBuffer[String]()

      var lenOfSchema = config.KafkaTopic.length - 1
      var lenOfSchemaforPostgres = config.postgresTopic.length - 1
      var lenOfNodeJs = config.NodeJsTopic.length - 1



      config.KafkaTopic.foreach { table =>

        if (!(lenOfSchemaforPostgres < 0)) {
          val postgresTopicNm = config.postgresTopic(lenOfSchemaforPostgres)
          postgresTopic += postgresTopicNm
        }
        if (!(lenOfSchema < 0)) {
          val KafkaTopic = config.KafkaTopic(lenOfSchema)
          mytopicList += KafkaTopic
        }



        lenOfSchemaforPostgres =lenOfSchemaforPostgres -1
        lenOfSchema=lenOfSchema-1


      }


      PostgresToKafka.writePostgresToKafka(spark,mytopicList, postgresTopic, kafkaDestIP, config,kafkaSourceeIP)



    })

  }

  }

