package io.functionaldata.streaming.config

/**
  * Created by mbarak on 15/07/16.
  */
case class TransactionsStreamingConfig(seconds : Int = 10)

object TransactionsStreamingConfig {
    private[this] val BlankConfig = new TransactionsStreamingConfig()

    private[this] val Parser = new scopt.OptionParser[TransactionsStreamingConfig]("scopt") {
      head("scopt", "3.x")

      opt[Int]("seconds")
        .action { case (seconds, c) => c.copy(seconds = seconds) }
        .text("seconds define the streaming window")
    }

  def parse(args: Array[String]) = Parser.parse(args, BlankConfig).getOrElse(BlankConfig)
}