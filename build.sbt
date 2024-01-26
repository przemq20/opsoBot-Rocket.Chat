name := "opsoBot"

version := "0.1"

scalaVersion := "2.13.3"

enablePlugins(JavaAppPackaging)

mainClass in Compile := Some("opsobot.Main")


resolvers += "scalac repo" at "https://raw.githubusercontent.com/ScalaConsultants/mvn-repo/master/"
val AkkaVersion = "2.8.0"
val AkkaHttpVersion = "10.5.0"
libraryDependencies ++= Seq("org.jsoup" % "jsoup" % "1.15.4",
  "io.spray" %% "spray-json" % "1.3.6",
  "com.github.slack-scala-client" %% "slack-scala-client" % "0.4.3",
  "org.slf4j" % "slf4j-api" % "2.0.5",
  "org.slf4j" % "slf4j-simple" % "2.0.5" % Provided,
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.4-akka-2.6.x",
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.outr" %% "scribe" % "3.11.2",
  "ch.qos.logback" % "logback-classic" % "1.4.7" % Runtime,
  "org.apache.tika" % "tika-core" % "2.7.0",
  "org.apache.tika" % "tika-parsers" % "2.7.0" pomOnly(),
  "org.apache.tika" % "tika-parser-pdf-module" % "2.7.0"
)
