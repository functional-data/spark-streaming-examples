import sbt.Keys._

name := """spark-steaming"""

version := "1.0"

scalaVersion := "2.11.7"



val sparkVersion =  "1.6.2"

// at the time of writing spark 1.6.2 supports up to 0.8.2.1 since it is compatible
val kafkaVersion = "0.8.2.1"

val confluentVersion = "2.0.0"

resolvers ++= Seq(
  "confluent" at "http://packages.confluent.io/maven/",
  Resolver.sonatypeRepo("public")
)


val sparkDeps = Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion excludeAll ExclusionRule(name = "jackson-databind"),
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-kafka" % sparkVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
  )


val avro = Seq(
  "org.apache.avro" % "avro" % "1.8.1"
)

val kafkaAvroSerde = Seq("io.confluent" % "kafka-avro-serializer" % confluentVersion excludeAll ExclusionRule(name = "jackson-databind"),
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
)

val kafkaClient = Seq("org.apache.kafka" % "kafka-clients" % kafkaVersion) ++ kafkaAvroSerde

val akka = Seq("com.typesafe.akka" %% "akka-actor" % "2.3.11")

val testDependencies =  Seq("org.scalatest" %% "scalatest" % "2.2.4" % "test")

val configParsing = Seq("com.github.scopt" %% "scopt" % "3.5.0")

lazy val domain = (
  Project("domain", file("domain"))
    settings(
      libraryDependencies ++= testDependencies ++ avro
    )
  )

lazy val ingestion = (
  Project("ingestion", file("ingestion"))
    settings(
      libraryDependencies ++= testDependencies ++ kafkaClient ++ akka,
      mainClass in (Compile, run) := Some("io.functionaldata.ingestion.Ingest")
    )
  ) dependsOn(domain)

lazy val streaming = (
    Project("streaming", file("streaming"))
      settings(
        libraryDependencies ++= testDependencies ++ sparkDeps ++ kafkaAvroSerde ++ configParsing,
        mainClass in (Compile, run) := Some("io.functionaldata.streaming.TransactionsStreaming")
      )
  ) dependsOn(domain)

dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
)

lazy val root = (project in file(".")).aggregate(streaming, ingestion, domain)


