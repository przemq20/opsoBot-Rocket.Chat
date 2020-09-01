package opsobot.utils

import org.slf4j.{Logger, LoggerFactory}
import scalaj.http.{Http, HttpOptions}

object RCUtils {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def sendToTheRocket(message: String): Unit = {
    val data = makeMessageString(message)
    val req = Http(RCEnvironment.SEND_MESSAGE).postData(data)
      .header("X-Auth-Token", Credentials.TOKEN)
      .header("X-User-Id", Credentials.USER_ID)
      .header("Content-type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000)).asString
    logger.info(req.body)
  }

  def makeMessageString(content: String): String = {
    val newlineChar = "\\n"
    val whitespaceChar = "\u2001"
    val rawContent = content
      .replace("\n", newlineChar)
      .replace("\t", whitespaceChar * 3)

    s"""{"message": {"rid": "${RCEnvironment.ROOM_ID}", "avatar": "${Credentials.AVATAR}", "msg": "$rawContent ", "alias": "OpsoBot"}}"""
  }
}
