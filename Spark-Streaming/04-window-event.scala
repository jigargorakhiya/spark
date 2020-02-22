#### KAFKA ####

//Create topic in Kafka
kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic topic1

//Listen to topic
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic topic1 --from-beginning

################

spark-shell --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.3.0

/**
* Programs Start
*
*/

import org.apache.spark.sql._
import org.apache.spark.sql.types._ 

val userSchema = new StructType()
 .add("fruit", "string")
 .add("size", "string")
 .add("color", "string")
 .add("Time", "string")

val iot = spark.readStream.format("json")
 .schema(userSchema)
 .option("path", "file:///Users/jigar/spark/Spark-Examples/Spark-Streaming/input").load()
 
val iot_group_win = iot.groupBy(window(col("Time"), "10 minutes")).count()

val iot_key_val = iot_group_win.withColumn("key", lit(100)).select(concat(col("window.start").cast("string"), lit(" to "),col("window.end").cast("string"), lit(" - ") ,col("count")).alias("value"))

//Aggregate queries - complete mode

val checkpointLocation = "file:///Users/jigar/spark/Spark-Examples/Spark-Streaming/chkpt"
val kafkaServer = "localhost:9092"
val topic = "topic1"

//Non aggregate queries - complete mode 
 
val stream = iot_key_val.writeStream
 .format("kafka")
 .option("kafka.bootstrap.servers", kafkaServer)
 .option("topic", topic)
 .option("checkpointLocation", checkpointLocation)
 .outputMode("complete")
 .start()
 
 //Aggregate queries - update mode

val stream = iot_key_val.writeStream
 .format("kafka")
 .option("kafka.bootstrap.servers", kafkaServer)
 .option("topic", topic)
 .option("checkpointLocation", checkpointLocation)
 .outputMode("update")
 .start()
 

//Aggregate queries won't support in append mode without watermark
 
val stream = iot_key_val.writeStream
 .format("kafka")
 .option("kafka.bootstrap.servers", kafkaServer)
 .option("topic", topic)
 .option("checkpointLocation", checkpointLocation)
 .outputMode("update")
 .start()


