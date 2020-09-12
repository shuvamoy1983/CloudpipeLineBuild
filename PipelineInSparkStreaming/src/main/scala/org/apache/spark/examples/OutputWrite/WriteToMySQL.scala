package org.apache.spark.examples.OutputWrite

import org.apache.spark.sql.DataFrame
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

object WriteToMySQL {

  def MySqlWrite(df: DataFrame,table:String,user:String,password:String,Host:String)(implicit xc:ExecutionContext) :Future[Unit]= Future {
    println("MySQL Writing", table,Host, user,password)
    val prop = new java.util.Properties
    prop.setProperty("driver", "com.mysql.jdbc.Driver")
    prop.setProperty("user", user)
    prop.setProperty("password", password)
    val url = s"jdbc:mysql://$Host:3306/mydb"

    df.write.mode("append").jdbc(url, table, prop)
    Future.successful(())
  }

}
