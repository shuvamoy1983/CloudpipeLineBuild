package org.apache.spark.examples

import org.apache.spark.examples.LogInfoDetails.getLogInfo._
import org.apache.spark.examples.TimezoneCalculate.getTimeZone._
import org.apache.spark.examples.allInputFiles.ListOfInputFile
import org.apache.spark.examples.FileParsing.fileParse._
import org.apache.spark.examples.SourceConfiguration.Configurations
import org.apache.spark.examples.sparkSession.ConnectSession
import org.apache.spark.examples.sparkSession.ConnectSession._
import org.apache.spark.examples.OutputWrite.OutputMapToDownstreamApp._
import org.apache.spark.examples.Utils.readFileFromResource

import scala.collection.mutable.ListBuffer
import org.apache.spark.examples.OutputWrite.{OutputMapToDownstreamApp, OutputTest}
//import org.apache.spark.examples.localtest.writeKafkaToMultipleApp1

object MainJob {
  def main(args: Array[String]): Unit = {
    getLog.info("Gcp Data migration Job Started")
    val startTime=convertToIST
    val ListOfFiles=ListOfInputFile.ListOfFilesToRead

   /* val kafkaIP="34.86.183.222"
    val cassandraHost="34.86.89.154"
    val cassandraUserName="cluster1-superuser"
    val cassandraPassword="nae1sTSNQRw_28Jte8E2nvLabYHAYePGnFq7Lc_iyH5yRbWAStN2Dg"
    val MySqlHost="34.86.56.34"
    val MySqlUserName="root"
    val MySqlPassword="password" */


    val kafkaIP=args(0)
    val cassandraHost=args(1)
    val cassandraUserName=args(2)
    val cassandraPassword=args(3)
    val MySqlHost=args(4)
    val MySqlUserName=args(5)
    val MySqlPassword=args(6)

    // Execution Started for each input File
    ListOfFiles.foreach({ resourcePath =>
      val tmpLoc = readFileFromResource.readFromInputResource(resourcePath)
      val config: Configurations = parseYamlFile(s"$tmpLoc")

      ConnectSession.init(config,MySqlHost,MySqlUserName,MySqlPassword,cassandraHost,cassandraUserName,cassandraPassword)

      val mytopicList =new ListBuffer[String]()
      val mySchemaList =new ListBuffer[String]()


        var lenOfSchema=config.KafkaSchemaDataSetName.length -1

        //writeKafkaToMultipleApp()
        config.KafkaSchemaDataSetName.foreach { schema =>
          val KafkaTopic = config.KafkaTopic(lenOfSchema)
          val schemainfo = config.KafkaSchemaDataSetName(lenOfSchema)

          mytopicList +=KafkaTopic
          mySchemaList +=schemainfo



         // Thread.sleep(4000L)
          lenOfSchema = lenOfSchema - 1
          //lenOfSchemaforPostgres=lenOfSchemaforPostgres -1
        }


      OutputMapToDownstreamApp.writeKafkaToMultipleApp(mytopicList, mySchemaList,kafkaIP,config)

    })

  }
}
