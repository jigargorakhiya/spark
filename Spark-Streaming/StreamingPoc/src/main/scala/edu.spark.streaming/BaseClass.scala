package edu.spark.streaming

import org.apache.spark.sql.SparkSession

class BaseClass {

  def createSparkSession(app_name:String) ={

    val spark = SparkSession.builder().appName(app_name).master("local[*]").getOrCreate()

    spark
  }

}
