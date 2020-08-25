package opsobot.bot

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import java.time.temporal.ChronoUnit
import java.time.{DayOfWeek, LocalDate, LocalTime}
import java.util.Calendar

import akka.actor.ActorSystem
import org.slf4j.{Logger, LoggerFactory}
import scalaj.http.{Http, HttpOptions}
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json._
import opsobot.bot.CommandParser.makePretty
import opsobot.parsers.{OlimpParser, OpsoParser}
import resources.{TEST_ENV_CREDENTIALS, TEST_ENV_RC_API}

object Bot {
  import system.dispatcher
  implicit val system: ActorSystem = ActorSystem("RocketChat")

  var channels: ListBuffer[String] = ListBuffer[String]("")
  val logger: Logger = LoggerFactory.getLogger(Bot.getClass)

  final val CREDENTIALS:  Credentials = TEST_ENV_CREDENTIALS
  final val RC_API:       RocketChatApiUrl = TEST_ENV_RC_API

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
      logger.info("Thread sending daily updates started")
      while (true) {
        val currentDayOfWeek = LocalDate.now.getDayOfWeek
        val currentTime = LocalTime.now.truncatedTo(ChronoUnit.SECONDS)
        val localizedDay = Locale.dayOfWeek(currentDayOfWeek)

        if (currentDayOfWeek == DayOfWeek.TUESDAY
          || currentDayOfWeek == DayOfWeek.THURSDAY
          || currentDayOfWeek == DayOfWeek.FRIDAY) {

          val tenOClock = LocalTime.of(10, 23, 30)
          if (currentTime == tenOClock) {
            val greeting = returnMessage(s"Witaj w $localizedDay! Dzisiaj możesz zamówić PIZZUNIĘ w OPSO. Ponadto, menu na dzisiaj to:")
            sendToTheRocket(greeting)
            sendMenus()
            logger.info("Menu sent")

          }
        } else if (currentDayOfWeek == DayOfWeek.MONDAY
          || currentDayOfWeek == DayOfWeek.WEDNESDAY) {
          val elevenOClock = LocalTime.of(11, 0, 0)
          if (currentTime == elevenOClock) {
            val greeting = returnMessage(s"Witaj w $localizedDay! Menu na dzisiaj to:")
            sendToTheRocket(greeting)
            sendMenus()
            logger.info("Menu sent")

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
    logger.info(s"Sent menu, date: ${Calendar.getInstance().getTime}")
  }

  def sendMenus(): Unit = {
    sendMenu("OPSO", makePretty(OpsoParser.parse().sort()))
    sendMenu("Olimp", makePretty(OlimpParser.parse().sort()))
  }

  //  def sendToTheRocket(message: String): Unit = {
  //    val req = Http("https://chat.czk.comarch.com/api/v1/chat.sendMessage").postData(message)
  //      .header("X-Auth-Token", token)
  //      .header("X-User-Id", user)
  //      .header("Content-type", "application/json")
  //      .header("Charset", "UTF-8")
  //      .option(HttpOptions.readTimeout(10000)).asString
  //    logger.info(req.body)
  //  }
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
    val rawMessage = message.replace("\n", "\\n").replace("\t", "\u2001" * 3)
    val mess = "{\"message\": {\"rid\": \"" +
      RC_API.ROOM_ID +
      "\",\"avatar\": \"https://avatars.slack-edge.com/2020-08-07/1281096213222_ad3d6fc601b6e272eb7e_512.png\"," +
      " \"msg\": \"" +
      rawMessage +
      " \", \"alias\":\"OpsoBot\" }}"
    mess
  }

  def getLastMessage: (String, String) = {
    //    val req = Http(s"https://chat.czk.comarch.com/api/v1/rooms.info?roomId=$room")
    val req = Http(RC_API.ROOM_ID)
      //      .header("X-Auth-Token", token)
      //      .header("X-User-Id", user)
      .header("X-Auth-Token", CREDENTIALS.TOKEN)
      .header("X-User-Id", CREDENTIALS.USER_ID)
      .header("Content-type", "application/json")
      .header("Charset", "UTF-8")

    //    val id = req.body.toJson.asJsObject().getFields("room").head.toJson.asJsObject().getFields("_id").head.toString()
    val lastMessage = req.asString.body.parseJson.asJsObject.getFields("room").head
      .asJsObject.getFields("lastMessage").head
    val id = lastMessage.asJsObject.getFields("_id").head.convertTo[String]
    val msg = lastMessage.asJsObject.getFields("msg").head.convertTo[String]
    //    val uID = lastMessage.asJsObject.getFields("u").head.asJsObject.getFields("_id").head.convertTo[String]

    (id, msg)
  }
}
