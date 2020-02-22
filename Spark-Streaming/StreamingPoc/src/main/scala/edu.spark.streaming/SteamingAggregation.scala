package edu.spark.streaming

import org.apache.spark.sql.streaming.OutputMode
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructType, TimestampType}
import org.apache.spark.sql.functions.sum

class SteamingAggregation(appname:String) extends BaseClass{

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
        .option("maxFilePerTrigger","2") // it will read max 2 file per batch
      .schema(retailSchema)
      .csv(inputPath)

    val filterByQty = streamingData
        .filter("Quantity > 10")
        .groupBy("InvoiceDate","Country")
      .agg(sum("UnitPrice"))

    val query = filterByQty.writeStream
      .format("console")
      .queryName("salesAggregateData")
      .outputMode(OutputMode.Complete())
      .start()

    query.awaitTermination()
  }



}

object SteamingAggregation {

  def main(args: Array[String]): Unit = {

    val sparkStreaming = new SteamingAggregation("SparkStreamingAggregation")

    sparkStreaming.run()

  }

}
