package org.apache.spark.examples.OutputWrite

import org.apache.spark.sql.DataFrame
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object WriteToCassanDra {

  def CassandraWrite(df: DataFrame,table:String )(implicit xc:ExecutionContext) :Future[Unit]= Future {
    println("Cassandra Writing")
    df.write
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> table, "keyspace" -> "mydb"))
      .mode("APPEND")
      .save()
    Future.successful(())
  }
}
