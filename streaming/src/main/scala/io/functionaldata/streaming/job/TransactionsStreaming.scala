package io.functionaldata.streaming.job

import io.confluent.kafka.serializers.KafkaAvroDecoder
import io.functionaldata.domain.BankTransaction
import io.functionaldata.streaming.config.TransactionsStreamingConfig
import org.apache.avro.specific.SpecificData
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by mbarak on 12/07/16.
  */
class TransactionsStreaming(val ssc: StreamingContext) {

  def job = {
    import TransactionsStreaming._

    val bankTransactionStream = KafkaUtils.createDirectStream[Object, Object, KafkaAvroDecoder, KafkaAvroDecoder](ssc, KafkaParams, Topic)

    bankTransactionStream.map{ case (k,transation) => toBankTransaction(transation)}
  }

  def toBankTransaction(transaction: Object): BankTransaction = SpecificData.get().deepCopy(BankTransaction.SCHEMA$, transaction).asInstanceOf[BankTransaction]
}

object TransactionsStreaming {
  def main(args: Array[String]) {

    val sparkConf = new SparkConf().setAppName("kafka-streaming-test").setMaster("local[*]")

    val appConf = TransactionsStreamingConfig.parse(args)

    val ssc = new StreamingContext(sparkConf, Seconds(appConf.seconds))

    val job = (new TransactionsStreaming(ssc)).job

    job.print()

    ssc.start()
    ssc.awaitTermination()
  }

  val KafkaParams = Map("metadata.broker.list" -> "localhost:9092",
    "schema.registry.url" -> "http://localhost:8081",
    "zookeeper.connect" -> "localhost:2181",
    "group.id" -> "kafka-spark-transactions-streaming"
  )

  val Topic = Set("transaction")
}
