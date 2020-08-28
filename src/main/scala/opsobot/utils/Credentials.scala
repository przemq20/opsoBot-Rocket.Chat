package opsobot.utils

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Properties
import scala.util.Properties.envOrElse

import scala.sys.process._

object Credentials {
  val credentialsFile: String = scala.util.Properties.envOrElse("CREDENTIALS", "test_env.conf")
  private final val config: Config = ConfigFactory
    .load(credentialsFile)
    .getConfig("opsobot.credentials")

  final val TOKEN: String = config.getString("token")
  final val USER_ID: String = config.getString("user_id")
  final val AVATAR: String = config.getString("avatar")
}
