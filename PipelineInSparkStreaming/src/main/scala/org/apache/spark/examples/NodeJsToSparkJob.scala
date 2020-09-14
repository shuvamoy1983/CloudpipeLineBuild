package org.apache.spark.examples

import java.util.concurrent.Executors

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


object NodeJsToSparkJob {

  def main(args: Array[String]): Unit = {

    val ListOfFiles = ListOfInputFile.ListOfFilesToRead
    ListOfFiles.foreach({ resourcePath =>
      val tmpLoc = readFileFromResource.readFromInputResource(resourcePath)
      val config: Configurations = parseYamlFile(s"$tmpLoc")

      val kafkaDestIP = args(0)
      val cassandraHost = args(1)
      val cassandraUserName = args(2)
      val cassandraPassword = args(3)
      val MySqlHost = args(4)
      val MySqlUserName = args(5)
      val MySqlPassword = args(6)
      val ElasticSearchIP = Option(args(7)).getOrElse("")


      ConnectSession.init(config, MySqlHost, MySqlUserName, MySqlPassword, cassandraHost, cassandraUserName, cassandraPassword, ElasticSearchIP)
      val spark: SparkSession = getSparkSession

      val postgresTopic = new ListBuffer[String]()
      val mytopicList = new ListBuffer[String]()
      val nodejsTopicList = new ListBuffer[String]()


      var lenOfNodeJs = config.NodeJsTopic.length - 1


      config.NodeJsTopic.foreach { table =>

        if (!(lenOfNodeJs < 0)) {
          val nodeJsTopic = config.NodeJsTopic(lenOfNodeJs)
          nodejsTopicList += nodeJsTopic
        }

        lenOfNodeJs = lenOfNodeJs - 1

      }

      NodeJsToSpark.writeNodeJsToKafka(spark, mytopicList, nodejsTopicList, kafkaDestIP, config)

    })
    }

}
