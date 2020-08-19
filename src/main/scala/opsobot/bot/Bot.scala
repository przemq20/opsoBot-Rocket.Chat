package opsobot.bot

import java.time.temporal.ChronoUnit
import java.time.{DayOfWeek, LocalDate, LocalTime}
import java.util.Calendar

import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json._
import akka.actor.ActorSystem
import opsobot.bot.CommandParser.{greetings, makePretty}
import opsobot.parsers.{OlimpParser, OpsoParser}
import org.slf4j.{Logger, LoggerFactory}
import resources.Credentials.{token, user}
import scalaj.http.{Http, HttpOptions}
import slack.SlackUtil
import slack.rtm.SlackRtmClient

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

object Bot {
  var channels: ListBuffer[String] = ListBuffer[String]("")
  implicit val system: ActorSystem = ActorSystem("RocketChat")
  val logger: Logger = LoggerFactory.getLogger(Bot.getClass)

  import system.dispatcher

  //  val client: SlackRtmClient = SlackRtmClient(token)
  //
  def run() {
    //    Future {
    //      client.onMessage { message =>
    //        val mentionedIds = SlackUtil.extractMentionedIds(message.text)
    //        logger.info(s"Client ID: ${client.getState().self.id}")
    //        if (mentionedIds.contains(client.getState().self.id)) {
    //          CommandParser.greetings(message, client)
    //          val commands = message.text.split(" ").distinct
    //
    //          logger.info(s"I received commands: ${commands.mkString("Array(", ", ", ")")}")
    //          if (commands.length == 1) {
    //            client.sendMessage(message.channel, "Jeśli potrzebujesz pomocy wpisz \"@opsoolimpbot -help\"")
    //          }
    //          else {
    //            for (command <- commands) {
    //              CommandParser.parse(command, message, client)
    //            }
    //          }
    //        }
    //      }
    //    }


    Future {
      while (true) {
        val currentDayOfWeek = LocalDate.now.getDayOfWeek
        val currentTime = LocalTime.now.truncatedTo(ChronoUnit.SECONDS)
        val localizedDay = Locale.dayOfWeek(currentDayOfWeek)

        if (currentDayOfWeek == DayOfWeek.TUESDAY
          || currentDayOfWeek == DayOfWeek.THURSDAY
          || currentDayOfWeek == DayOfWeek.FRIDAY) {

          val tenOClock = LocalTime.of(10, 0, 0)
          if (currentTime == tenOClock) {
            val greeting = returnMessage(s"Cześć w $localizedDay! Dzisiaj możesz zamówić PIZZUNIĘ w OPSO. Ponadto, menu na dzisiaj to:")
            sendToTheRocket(greeting)
            sendMenus()

          }
        } else if (currentDayOfWeek == DayOfWeek.MONDAY
          || currentDayOfWeek == DayOfWeek.WEDNESDAY) {
          val elevenOClock = LocalTime.of(11, 0, 0)
          if (currentTime == elevenOClock) {
            val greeting = returnMessage(s"Cześć w $localizedDay! Menu na dzisiaj to:")
            sendToTheRocket(greeting)

            sendMenus()

          }
        }
        Thread.sleep(999)
      }
    }
  }

  def sendMenu(restaurant: String, menu: String): Unit = {
    val sb = new StringBuilder()
    sb.addAll(restaurant.toUpperCase)
    sb.addAll(" Menu:\n")
    sb.addAll("----" * restaurant.length)
    sb.addAll("\n")
    sb.addAll(menu)
    sb.addAll("\n")
    sb.addAll("-" * 40)
    sb.addAll("\n")
    val message = returnMessage(sb.result())
    sendToTheRocket(message)
    //    client.sendMessage(channel, sb.result())
    logger.info(s"Sent menu, date: ${Calendar.getInstance().getTime}")
  }

  def sendMenus(): Unit = {
    sendMenu("OPSO", makePretty(OpsoParser.parse().sort()))
    sendMenu("Olimp", makePretty(OlimpParser.parse().sort()))
  }

  def sendToTheRocket(message: String): Unit = {
    val req = Http("https://chat.czk.comarch.com/api/v1/chat.sendMessage").postData(message
    ).header("X-Auth-Token", token)
      .header("X-User-Id", user)
      .header("Content-type", "application/json")
      .header("Charset", "UTF-8").option(HttpOptions.readTimeout(10000)).asString
    println(req.body)
  }

  def returnMessage(message: String): String = {
    val rawMessage = message.replace("\n", "\\n").replace("\t", "\u2001" * 3)
    val mess = "{\"message\": {\"rid\": \"na2HFLXeRMXGJqpYT\", \"msg\": \"" +
      s" $rawMessage" +
      " \", \"alias\":\"OpsoBot\" }}"
    //    println(mess)
    mess
  }
}
