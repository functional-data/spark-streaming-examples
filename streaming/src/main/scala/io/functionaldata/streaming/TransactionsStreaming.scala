package io.functionaldata.streaming

import io.confluent.kafka.serializers.{KafkaAvroDecoder, KafkaAvroSerializer}
import org.apache.spark.streaming.kafka.KafkaUtils
import io.functionaldata.domain.BankTransaction
import org.apache.avro.generic.GenericRecord
import org.apache.avro.specific.SpecificData
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by mbarak on 12/07/16.
  */
class TransactionsStreaming {

  def run() = {

    val kafkaParams = Map("metadata.broker.list" -> "localhost:9092",
      "schema.registry.url" -> "http://localhost:8081",
      "zookeeper.connect" -> "localhost:2181",
      "group.id" -> "kafka-spark-transactions-streaming"
    )
    val topic = Set("transaction")

    val conf = new SparkConf().setAppName("kafka-streaming-test").setMaster("local[*]")

    val ssc = new StreamingContext(conf, Seconds(10))

    val bankTransactionStream = KafkaUtils.createDirectStream[Object, Object, KafkaAvroDecoder, KafkaAvroDecoder](ssc, kafkaParams, topic)

    bankTransactionStream.map{ case (k,v) => SpecificData.get().deepCopy(BankTransaction.SCHEMA$, v)}.print()

    ssc.start()
    ssc.awaitTermination()
  }
}

object TransactionsStreaming {
  def main(args: Array[String]) {
    val t = new TransactionsStreaming()
    t.run()
  }
}
