package io.functionaldata.ingestion

import java.util.Properties
import java.util.logging.LogManager

import akka.actor.Actor
import io.functionaldata.util.ResourceReader
import io.functionaldata.domain.BankTransaction
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import scala.util.Random

/**
  * Created by mbarak on 12/07/16.
  */


class TransactionsIngestionActor(val res: ResourceReader, val props: Properties) extends Actor {

  import TransactionsIngestionActor._
  val r = new Random()

  val lines = res.source.getLines().toList
    .map(parseLine)
    .groupBy(_.getDate.toString)
    .mapValues(r.shuffle(_))
    .toList
    .sortBy(_._1)
    .map(_._2)

  var index = 0

  val topic = "transaction"

  val producer = new KafkaProducer[Object, Object](props)

  val callBack = new MyKafkaCallBack(getClass.getName)

  override def receive: Receive = {
    case Ping if (index == lines.length) => context.system.shutdown()

    case Ping => {
      publish(lines(index))
      index += 1
    }
  }

  def publish(transactions: Iterable[BankTransaction]): Unit = {
    transactions.foreach(t => {
      val key = t.getBeneficiary

      val rec = new ProducerRecord[Object,Object](topic, key, t)
        producer.send(rec, callBack)
    })
  }
}


object TransactionsIngestionActor {
  val Ping = "Ping"
  val Last = "Last"

  def parseLine(line: String): BankTransaction = {
    val Array(date, beneficiary, amount, payment) = line.split("\t")

    new BankTransaction(date, beneficiary, amount.toInt, payment)
  }

  class MyKafkaCallBack(val name: String) extends Callback {
    val source = s"${name}_kafka_sink"
    val logger = LogManager.getLogManager.getLogger(source)
    override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
      Option(exception).foreach(e => logger.throwing(source, "publish", exception))
    }
  }
}



