package org.apache.spark.examples.schmaJson

import org.apache.spark.sql.types.{IntegerType, StringType, StructType}

object JsonStructures {


   def Schema(table: String) : org.apache.spark.sql.types.StructType  ={
     var strc : org.apache.spark.sql.types.StructType=null
     if (table.equals("emp")) {
       strc=getEmpSchema
       strc
     }
     else if (table.equals("dept")) {
       strc=getDeptSchema
       strc
     }
     strc
   }

  def getEmpSchema:org.apache.spark.sql.types.StructType = {
    val structureSchema = new StructType()
      .add("payload", new StructType()
        .add("after", new StructType()
          .add("id", IntegerType)
          .add("Name", StringType)
          .add("Salary", IntegerType)))
    structureSchema
  }

  def getDeptSchema:org.apache.spark.sql.types.StructType = {

    val structureSchema = new StructType()
      .add("payload", new StructType()
        .add("after", new StructType()
          .add("DEPT_ID", IntegerType)
          .add("Dept_Name", StringType)
          .add("EMP_ID", IntegerType)))
    structureSchema
  }

}
