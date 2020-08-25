package opsobot.bot

import java.time.{Duration, LocalDateTime, ZoneId}
import java.util.Date

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

import scala.concurrent.ExecutionContextExecutor

case object Tick

object StreamBot extends App {
  implicit val system: ActorSystem = ActorSystem("reader")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val scheduler = QuartzSchedulerExtension(system)
  val scheduleName = "Every5Seconds"

  val source = Source.actorRef(10, OverflowStrategy.dropHead)
  val ref: ActorRef = Flow[String].to(Sink.foreach(println)).runWith(source)

  // schedule returns date of first incoming event
  val firstScheduledDate = scheduler.schedule(scheduleName, ref, Tick)
  val firstScheduledLocalDT = dateToLocalDT(firstScheduledDate)

  val currentDT = LocalDateTime.now()
  val formattedTimeLeft = prettyTimeLeft(currentDT, firstScheduledLocalDT)
  println(s"$formattedTimeLeft left to send menu for the first time")

  def prettyTimeLeft(start: LocalDateTime, end: LocalDateTime): String = {
    val diff = Duration.between(start, end)
    String.format("%dh %02dm %02ds",
                  diff.toHours,
                  diff.toMinutesPart,
                  diff.toSecondsPart
    )
  }

  def dateToLocalDT(date: Date): LocalDateTime = {
    date.toInstant.atZone(ZoneId.systemDefault()).toLocalDateTime
  }
}
