package opsobot.streambot

import akka.NotUsed
import akka.actor.{ ActorRef, ActorSystem }
import akka.stream.scaladsl.{ Flow, Sink, Source }
import akka.stream.{ Materializer, OverflowStrategy }
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import opsobot.parsers._
import opsobot.utils.DateTimeUtils.printTimeLeft
import opsobot.utils.RCUtils.sendToTheRocket
import opsobot.utils.{ DateTimeUtils, Locale }

import java.time.{ DayOfWeek, LocalDate, LocalDateTime }
import scala.concurrent.ExecutionContextExecutor

class StreamBot {

  private def getGreetingsForToday(dayOfWeek: DayOfWeek, isPizzaday: Boolean): String = {
    val localizedDay = Locale.dayOfWeek(dayOfWeek)
    dayOfWeek match {
      case DayOfWeek.TUESDAY => s"Witaj we $localizedDay! Menu na dzisiaj to:"
      case _                 => s"Witaj w $localizedDay! Menu na dzisiaj to:"
    }
  }

  private def createMessage(restaurant: String, menu: Menu): String = {
    val sb = new StringBuilder()
    sb.addAll(restaurant.toUpperCase)
    sb.addAll(" Menu:\n")
    sb.addAll("----" * restaurant.length)
    sb.addAll("\n")
    sb.addAll(menu.toString)
//    sb.addAll("\n")
    sb.addAll("-" * 40)
    sb.addAll("\n")
    sb.result()
  }

  private def schedule(schedules: Seq[String], ref: ActorRef, msg: AnyRef)(implicit system: ActorSystem): LocalDateTime = {
    val scheduler   = QuartzSchedulerExtension(system)
    var closestDate = LocalDateTime.MAX
    schedules.foreach(schedule => {
      val date = scheduler.schedule(schedule, ref, msg)
      if (DateTimeUtils.dateToLocalDT(date).isBefore(closestDate)) {
        closestDate = DateTimeUtils.dateToLocalDT(date)
      }
    })
    closestDate
  }

  private val sendGreetings: Flow[Tick, Tick, NotUsed] = Flow[Tick].map { tick =>
    val dayOfWeek = LocalDate.now.getDayOfWeek
    scribe.info("Sending greetings")
    sendToTheRocket(getGreetingsForToday(dayOfWeek, isPizzaday = true))
    scribe.info(s"Greetings sent successfully at ${LocalDateTime.now()}")
    tick
  }

  private val scrapeMenusFlow: Flow[Tick, MenuMessage, NotUsed] =
    Flow[Tick].mapConcat { _ =>
      new MenuMessage("OPSO", OpsoParserPDF.parse()) ::
        new MenuMessage("Olimp", OlimpParser.parse()) ::
        new MenuMessage("Makarun", MakarunParser.parse()) ::
        new MenuMessage("Food Trucki", FoodTrucksParser.parse()) ::
        new MenuMessage("Bobek Burger", BobekParser.parse()) ::
      Nil
    }

  private val sendMenuFlow: Flow[MenuMessage, Unit, NotUsed] = Flow[MenuMessage].map { msg =>
    scribe.info(s"Creating menu message for ${msg.restaurant}")
    val rocketMessage = createMessage(msg.restaurant, msg.content)
//      .replaceAll("(?m)^[ \t]*\r?\n", "")
    scribe.info(s"Message created. Trying to send menu for ${msg.restaurant}")
    sendToTheRocket(rocketMessage)
    scribe.info(s"Sent successfully menu at: ${LocalDateTime.now()} for ${msg.restaurant}")
  }

  def run(): Unit = {
    implicit val system:       ActorSystem              = ActorSystem("reader")
    implicit val materializer: Materializer.type        = Materializer
    implicit val ec:           ExecutionContextExecutor = system.dispatcher

    val streamBotFlow =
      Flow[Tick]
        .via(sendGreetings)
        .via(scrapeMenusFlow)
        .via(sendMenuFlow)

    val source = Source.actorRef(10, OverflowStrategy.dropHead)
    val ref: ActorRef = streamBotFlow.to(Sink.ignore).runWith(source)

    val schedules      = List("MondaysAndWednesdays")
    val firstScheduled = schedule(schedules, ref, Tick)
    printTimeLeft(firstScheduled)
  }
}
