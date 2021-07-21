package opsobot.utils

import com.typesafe.config.{ Config, ConfigFactory }

object Credentials {
  final val configFile: String = scala.util.Properties.envOrElse("CREDENTIALS", "test.conf")
  private final val config: Config = ConfigFactory
    .load()
    .getConfig("opsobot.credentials")

  final val TOKEN:   String = config.getString("token")
  final val USER_ID: String = config.getString("user_id")
  final val AVATAR:  String = config.getString("avatar")
}
