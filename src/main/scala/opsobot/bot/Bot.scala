package opsobot.bot

import java.time.temporal.ChronoUnit
import java.time.{DayOfWeek, LocalDate, LocalTime}
import java.util.Calendar

import spray.json._
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import opsobot.bot.CommandParser.{greetings, makePretty}
import opsobot.parsers.{OlimpParser, OpsoParser}
import org.slf4j.{Logger, LoggerFactory}
import resources.Credentials.{token, user}
import scalaj.http.{Http, HttpOptions}
import slack.SlackUtil
import slack.rtm.SlackRtmClient
import spray.json.DefaultJsonProtocol.{RootJsObjectFormat, StringJsonFormat}

import scala.concurrent.duration._
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.util.control.Breaks.{break, breakable}

object Bot {
  var channels: ListBuffer[String] = ListBuffer[String]("")
  implicit val system: ActorSystem = ActorSystem("RocketChat")
  val logger: Logger = LoggerFactory.getLogger(Bot.getClass)
  val room = "na2HFLXeRMXGJqpYT"

  import system.dispatcher

  def run() {
    logger.info("OpsoBot started")
//    Future {
//      while (true) {
//
//        val lastMessage = getLastMessage
//        val lastId = lastMessage._1
//        breakable {
//          while (lastId != getLastMessage._1) {
//            val message = getLastMessage._2
//            if (message.contains("@opsoBot")) {
//              val commands = message.split(" ").toBuffer
//              commands -= "@opsoBot"
//              for(command <- commands) {
////                CommandParser.parse(command, )
//              }
//            }
//            break
//          }
//        }
//        Thread.sleep(100)
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
    val req = Http("https://chat.czk.comarch.com/api/v1/chat.sendMessage").postData(message)
      .header("X-Auth-Token", token)
      .header("X-User-Id", user)
      .header("Content-type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000)).asString
    println(req.body)
  }

  def returnMessage(message: String): String = {
    val rawMessage = message.replace("\n", "\\n").replace("\t", "\u2001" * 3)
    val mess = "{\"message\": {\"rid\": \"" +
      room +
      "\",\"avatar\": \"https://avatars.slack-edge.com/2020-08-07/1281096213222_ad3d6fc601b6e272eb7e_512.png\"," +
      " \"msg\": \"" +
      rawMessage +
      " \", \"alias\":\"OpsoBot\" }}"
        println(mess)
    mess
  }

  def getLastMessage: (String, String) = {
    val req = Http(s"https://chat.czk.comarch.com/api/v1/rooms.info?roomId=$room")
      .header("X-Auth-Token", token)
      .header("X-User-Id", user)
      .header("Content-type", "application/json")
      .header("Charset", "UTF-8")

    //    val id = req.body.toJson.asJsObject().getFields("room").head.toJson.asJsObject().getFields("_id").head.toString()
    val lastMessage = req.asString.body.parseJson.asJsObject.getFields("room").head
      .asJsObject.getFields("lastMessage").head
    val id = lastMessage.asJsObject.getFields("_id").head.convertTo[String]
    val msg = lastMessage.asJsObject.getFields("msg").head.convertTo[String]
    //    val uID = lastMessage.asJsObject.getFields("u").head.asJsObject.getFields("_id").head.convertTo[String]
    println(id, msg)

    (id, msg)
  }
}
