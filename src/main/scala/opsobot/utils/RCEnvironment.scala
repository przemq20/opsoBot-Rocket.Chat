package opsobot.utils

import com.typesafe.config.{Config, ConfigFactory}

object RCEnvironment {
  private final val config: Config = ConfigFactory
    .load("test_env.conf")
    .getConfig("opsobot.environment")
  final val HOST: String = config.getString("host")
  final val ROOM_ID: String = config.getString("room_id")
  final val API_PATH: String = "/api/v1"
  final val CORE_URL: String = s"$HOST$API_PATH"

  final val SEND_MESSAGE = s"$CORE_URL/chat.sendMessage"
  final val ROOMS_INFO = s"$CORE_URL/rooms.info?roomId=$ROOM_ID"
}
