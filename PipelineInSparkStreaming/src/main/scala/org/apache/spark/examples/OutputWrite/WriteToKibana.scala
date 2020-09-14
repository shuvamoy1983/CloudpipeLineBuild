package org.apache.spark.examples.OutputWrite

import org.apache.spark.sql.{DataFrame, SaveMode}
import org.elasticsearch.spark.sql._
import scala.concurrent.{ExecutionContext, Future}

object WriteToKibana {
  def writeTOKibanaIndex(df: DataFrame,index:String )(implicit xc:ExecutionContext) :Future[Unit]= Future {
    println(s"Writing to Kibanana")
      df.write.mode(SaveMode.Append).format("org.elasticsearch.spark.sql").save("/mypoc/_doc")
    Future.successful(())
  }
}
