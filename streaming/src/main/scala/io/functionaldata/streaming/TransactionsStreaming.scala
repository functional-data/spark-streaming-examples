package io.functionaldata.streaming

import org.apache.spark.streaming.kafka.KafkaUtils
import io.functionaldata.domain.BankTransaction

/**
  * Created by mbarak on 12/07/16.
  */
class TransactionsStreaming {

  def run(configs: Map[String,String]) = {

    val bankTransactionStream = KafkaUtils.createDirectStream[String, BankTransaction, io.confluent.kafka.serializers.KafkaAvroSerializer, io.confluent.kafka.serializers.KafkaAvroSerializer]
  }

}
