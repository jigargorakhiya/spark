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

val df = spark
  .readStream
  .format("kafka")
  .option("kafka.bootstrap.servers", "localhost:9092")
  .option("subscribe", "topic1")
  .load()

  val ds = df
  .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
  .writeStream
  .format("kafka")
  .option("kafka.bootstrap.servers", "localhost:9092")
  .option("topic", "topic1")
  .option("checkpointLocation", "file:///Users/jigar/Documents/Jigar/Hadoop")
  .start()