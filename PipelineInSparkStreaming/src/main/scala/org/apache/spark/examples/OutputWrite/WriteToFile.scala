package org.apache.spark.examples.OutputWrite

import org.apache.spark.sql.{DataFrame, SaveMode}

import scala.concurrent.{ExecutionContext, Future}

object WriteToFile {

  def WriteAsParquet(df: DataFrame,table:String,Buc:String )(implicit xc:ExecutionContext) :Future[Unit]= Future {
    println(s"File Writing to Storage to this location $Buc/$table")
    df.write.mode(SaveMode.Append).parquet(s"$Buc/$table/")
    Future.successful(())
  }

}
