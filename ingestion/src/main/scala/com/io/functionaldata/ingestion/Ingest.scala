package com.io.functionaldata.ingestion

import java.util.Properties

import akka.actor.{ActorSystem, Props}
import com.io.functionaldata.util.ClassPathResourceReader
import org.apache.kafka.clients.producer.ProducerConfig

import scala.concurrent.duration._

/**
  * Created by mbarak on 12/07/16.
  */
object Ingest {


  def main(args: Array[String]) {


    val system = ActorSystem("HelloSystem")
    implicit val ec = scala.concurrent.ExecutionContext.global

    val actorProps = Props( new TransactionsIngestionActor( new ClassPathResourceReader("transactions.tsv"), getConfiguration))

    val actor = system.actorOf(actorProps)


    system.scheduler.schedule(0 seconds, 30 seconds, actor, TransactionsIngestionActor.Ping)

    system.awaitTermination()
  }

  def getConfiguration: Properties = {

    val props = new Properties()

    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      classOf[io.confluent.kafka.serializers.KafkaAvroSerializer]);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      classOf[io.confluent.kafka.serializers.KafkaAvroSerializer]);
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put("schema.registry.url", "http://localhost:8081");

    props
  }
}
