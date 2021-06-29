name := "opsoBot"

version := "0.1"

scalaVersion := "2.13.3"

enablePlugins(JavaAppPackaging)

resolvers += "scalac repo" at "https://raw.githubusercontent.com/ScalaConsultants/mvn-repo/master/"
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"
libraryDependencies ++= Seq("org.jsoup" % "jsoup" % "1.13.1",
  "io.spray" %% "spray-json" % "1.3.5",
  "com.github.slack-scala-client" %% "slack-scala-client" % "0.2.10",
  "org.slf4j" % "slf4j-simple" % "1.6.4" % Provided,
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.4-akka-2.6.x",
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion

)
