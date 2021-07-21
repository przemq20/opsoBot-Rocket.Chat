package opsobot.utils

import scalaj.http.{Http, HttpOptions}

object RCUtils {

  def sendToTheRocket(message: String): Unit = {
    val data = makeMessageString(message)
    try Http(RCEnvironment.SEND_MESSAGE).postData(data)
      .header("X-Auth-Token", Credentials.TOKEN)
      .header("X-User-Id", Credentials.USER_ID)
      .header("Content-type", "application/json")
      .header("Charset", "UTF-8")
      .option(HttpOptions.readTimeout(10000)).asString
    catch {
      case e: Throwable => scribe.error(s"Error: ${e.getLocalizedMessage}")
    }
  }

  def makeMessageString(content: String): String = {
    val newlineChar    = "\\n"
    val whitespaceChar = "\u2001"
    val rawContent = content
      .replace("\n", newlineChar)
      .replace("\t", whitespaceChar * 3)

    s"""{"message": {"rid": "${RCEnvironment.ROOM_ID}", "avatar": "${Credentials.AVATAR}", "msg": "$rawContent ", "alias": "OpsoBot"}}"""
  }
}
