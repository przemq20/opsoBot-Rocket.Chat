package opsobot.streambot

import java.time.{Duration, LocalDate, LocalDateTime, ZoneId}
import java.util.Date

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import opsobot.parsers.Menu
import opsobot.utils.{Credentials, Locale, RocketChatApiUrl}
import org.slf4j.{Logger, LoggerFactory}
import resources.PROD_ENV_CREDENTIALS.AVATAR
import resources.{TEST_ENV_CREDENTIALS, TEST_ENV_RC_API}
import scalaj.http.{Http, HttpOptions}

import scala.concurrent.ExecutionContextExecutor

object StreamBot extends App {
  implicit val system: ActorSystem = ActorSystem("reader")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val logger: Logger = LoggerFactory.getLogger(StreamBot.getClass)

  final val CREDENTIALS:  Credentials = TEST_ENV_CREDENTIALS
  final val RC_API:       RocketChatApiUrl = TEST_ENV_RC_API

  val scheduler = QuartzSchedulerExtension(system)
  val scheduleName = "Every10Seconds"
  val scheduleName2 = "Every10Seconds2"

  val sendGreeting: Flow[MenuMessage, MenuMessage, NotUsed] =
    Flow[MenuMessage].map(sendGreetings)

  val source = Source.actorRef(10, OverflowStrategy.dropHead)
  val ref: ActorRef = sendGreeting.to(Sink.foreach{msg => println(msg.content)}).runWith(source)

  val msg = PizzadayMenuMessage(new Menu()
    .addToCategory("Zupy", "Pomidorowa" :: "Ogórkowa" :: Nil)
    .addToCategory("Dodatki", "Frytki" :: "Ryż" :: Nil)
    .addToCategory("Dania wegańskie", "Gnocchi z serem" :: Nil)
  )
  val msg2 = NoPizzadayMenuMessage(new Menu()
    .addToCategory("Zupy", "Grzybowa" :: "Szczawiowa" :: Nil)
    .addToCategory("Dania wegańskie", "Pizza z makaronem" :: Nil)
  )

  val firstScheduledDate = scheduler.schedule(scheduleName, ref, msg)
  val secondScheduledDate = scheduler.schedule(scheduleName2, ref, msg2)
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

  def sendGreetings(msg: MenuMessage): MenuMessage = {
    msg match {
      case PizzadayMenuMessage(_) =>
        sendToTheRocket(getGreetingsForToday(isPizzaday = true))
      case NoPizzadayMenuMessage(_) =>
        sendToTheRocket(getGreetingsForToday(isPizzaday = false))
    }
    msg
  }

  def getGreetingsForToday(isPizzaday: Boolean): String = {
    val currentDayOfWeek = LocalDate.now.getDayOfWeek
    val localizedDay = Locale.dayOfWeek(currentDayOfWeek)
    if (isPizzaday) returnMessage(s"Witaj w $localizedDay! Menu na dzisiaj to:")
    else returnMessage(s"Witaj w $localizedDay! Dzisiaj możesz zamówić PIZZUNIĘ w OPSO. Ponadto, menu na dzisiaj to:")
  }

  def sendToTheRocket(message: String): Unit = {
    val req = Http(RC_API.SEND_MESSAGE).postData(message)
      .header("X-Auth-Token", CREDENTIALS.TOKEN)
      .header("X-User-Id", CREDENTIALS.USER_ID)
      .header("Content-type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000)).asString
    logger.info(req.body)
  }

  def returnMessage(message: String): String = {
    val whitespaceChar = "\u2001"
    val newlineChar = "\\n"
    val rawMessage = message
      .replace("\n", newlineChar)
      .replace("\t", whitespaceChar * 3)

    s"""{"message": {"rid": "${RC_API.ROOM_ID}", "avatar": "${AVATAR}", "msg": "$rawMessage ", "alias": "OpsoBot"}}"""
  }

}
