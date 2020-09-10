package opsobot.streambot

import java.time.{DayOfWeek, LocalDate, LocalDateTime}

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import com.typesafe.config.{Config, ConfigFactory}
import opsobot.parsers.{Menu, OlimpParser, OpsoParser}
import opsobot.utils.DateTimeUtils.printTimeLeft
import opsobot.utils.{DateTimeUtils, Locale}
import opsobot.utils.RCUtils.sendToTheRocket
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContextExecutor

class StreamBot {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  private def getGreetingsForToday(dayOfWeek: DayOfWeek, isPizzaday: Boolean): String = {
    val localizedDay = Locale.dayOfWeek(dayOfWeek)
    if (isPizzaday) s"Witaj w $localizedDay! Menu na dzisiaj to:"
    else s"Witaj w $localizedDay! Dzisiaj możesz zamówić PIZZUNIĘ w OPSO. Ponadto, menu na dzisiaj to:"
  }

  private def createMessage(restaurant: String, menu: Menu): String = {
    val sb = new StringBuilder()
    sb.addAll(restaurant.toUpperCase)
    sb.addAll(" Menu:\n")
    sb.addAll("----" * restaurant.length)
    sb.addAll("\n")
    sb.addAll(menu.toString)
    sb.addAll("\n")
    sb.addAll("-" * 40)
    sb.addAll("\n")
    sb.result()
  }

  private def schedule(schedules: Seq[String], ref: ActorRef, msg: AnyRef)(implicit system: ActorSystem): LocalDateTime = {
    val scheduler = QuartzSchedulerExtension(system)
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
    if (dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.WEDNESDAY) {
      sendToTheRocket(getGreetingsForToday(dayOfWeek, isPizzaday = true))
    } else {
      sendToTheRocket(getGreetingsForToday(dayOfWeek, isPizzaday = false))
    }
    tick
  }

  private val scrapeMenusFlow: Flow[Tick, MenuMessage, NotUsed] =
    Flow[Tick].mapConcat { _ =>
      new MenuMessage("OPSO", OpsoParser.parse()) ::
        new MenuMessage("Olimp", OlimpParser.parse()) ::
        Nil
    }

  private val sendMenuFlow: Flow[MenuMessage, Unit, NotUsed] = Flow[MenuMessage].map { msg  =>
    val rocketMessage = createMessage(msg.restaurant, msg.content)
    sendToTheRocket(rocketMessage)
    logger.info(s"Sent menu at: ${LocalDateTime.now()}")
  }


  def run(): Unit = {
    implicit val system: ActorSystem = ActorSystem("reader")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContextExecutor = system.dispatcher

    val streamBotFlow =
      Flow[Tick]
        .via(sendGreetings)
        .via(scrapeMenusFlow)
        .via(sendMenuFlow)

    val source = Source.actorRef(10, OverflowStrategy.dropHead)
    val ref: ActorRef = streamBotFlow.to(Sink.ignore).runWith(source)

    val schedules = List("MondaysAndWednesdays", "PizzaDays")
    val firstScheduled = schedule(schedules, ref, Tick)
    printTimeLeft(firstScheduled)
  }
}
