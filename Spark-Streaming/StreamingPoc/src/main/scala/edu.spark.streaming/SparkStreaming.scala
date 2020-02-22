package edu.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructType, TimestampType}
import org.apache.spark.streaming.StreamingContext

class SparkStreaming(appname:String) extends BaseClass{

  val spark = createSparkSession(appname)

  def run(): Unit ={

    spark.sparkContext.setLogLevel("ERROR")

    val inputPath = "/Users/jigar/spark/Spark-Examples/Spark-Streaming/StreamingPoc/src/main/resources/dataset"

    val retailSchema = new StructType()
      .add("InvoiceNo",StringType)
      .add("StockCode",StringType)
      .add("Description",StringType)
      .add("Quantity",IntegerType)
      .add("InvoiceDate",StringType)
      .add("UnitPrice",DoubleType)
      .add("CustomerID",IntegerType)
      .add("Country",StringType)
      .add("InvoiceTimestamp",StringType)

    val streamingData = spark.readStream
        .schema(retailSchema)
        .csv(inputPath)

    val filterByCountry = streamingData.filter("Country='United Kingdom' ")

    val query = filterByCountry.writeStream
      .format("console")
      .queryName("filteredByCountry")
      .outputMode(OutputMode.Update())
      .start()

    query.awaitTermination()
  }



}

object SparkStreaming {

  def main(args: Array[String]): Unit = {

    val sparkStreaming = new SparkStreaming("SparkStreaming")

    sparkStreaming.run()

  }

}
