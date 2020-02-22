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

val iot = spark.readStream.format("json")
 .schema(userSchema)
 .option("path", "file:///Users/jigar/spark/Spark-Examples/Spark-Streaming/input").load()

val iot_key_val = iot.withColumn("key", lit(100))
 .select(concat(col("fruit"),lit(" "),col("size"), lit("  "), col("color"), lit(" ")).alias("value"))

val checkpointLocation = "file:///Users/jigar/spark/Spark-Examples/Spark-Streaming/chkpt"
val kafkaServer = "localhost:9092"
val topic = "topic1"

//Non aggregate queries - append mode 
 
val stream = iot_key_val.writeStream
 .format("kafka")
 .option("kafka.bootstrap.servers", kafkaServer)
 .option("topic", topic)
 .option("checkpointLocation", checkpointLocation)
 .outputMode("append")
 .start()


//Non aggregate queries - update mode

val stream = iot_key_val.writeStream
 .format("kafka")
 .option("kafka.bootstrap.servers", kafkaServer)
 .option("topic", topic)
 .option("checkpointLocation", checkpointLocation)
 .outputMode("update")
 .start()
 
//Non aggregate queries - complete mode

// Spark doesnot support non-aggregate (simple select) query with with complete mode because spark has to
// keep all the records in result table and it will be impact when result table grows in size.
val stream = iot_key_val.writeStream
 .format("kafka")
 .option("kafka.bootstrap.servers", kafkaServer)
 .option("topic", topic)
 .option("checkpointLocation", checkpointLocation)
 .outputMode("complete")
 .start()