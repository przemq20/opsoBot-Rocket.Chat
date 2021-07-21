package opsobot.utils

import com.typesafe.config.{ Config, ConfigFactory }
import scalaj.http.Http
import spray.json._ // if you don't supply your own Protocol (see below)

object RCEnvironment {
  final val configFile: String = scala.util.Properties.envOrElse("CREDENTIALS", "test.conf")
  private final val config: Config = ConfigFactory
    .load()
    .getConfig("opsobot.environment")

  final val HOST:     String = config.getString("host")
  final val ROOM_ID:  String = config.getString("room_id")
  final val API_PATH: String = "/api/v1"

  final val CORE_URL: String = s"$HOST$API_PATH"
  final val SEND_MESSAGE = s"$CORE_URL/chat.sendMessage"
  final val ROOMS_INFO   = s"$CORE_URL/rooms.info?roomId=$ROOM_ID"

  lazy val ROOM_NAME: String = {
    val request = Http("https://chat.czk.comarch.com/api/v1/groups.info?roomId=CESJrJsPqoDEB67mC")
      .header("X-Auth-Token", Credentials.TOKEN)
      .header("X-User-Id", Credentials.USER_ID)
      .header("Content-type", "application/json")
      .header("Charset", "UTF-8")
      .asString
      .body

    val result = request.parseJson.asJsObject.fields.get("group") match {
      case Some(value) => value.asJsObject.fields.get("name")
      case None        => None
    }

    result.map(a => a.toString().filter(s => s != '\"')).getOrElse("Brak nazwy grupy")
  }
}
