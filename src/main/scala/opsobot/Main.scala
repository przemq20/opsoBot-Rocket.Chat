package opsobot

import opsobot.bot.Bot
import opsobot.bot.TestBot
import opsobot.parsers.{OlimpParser, OpsoParser}

object Main {
  def main(args: Array[String]): Unit = {
    val bot = Bot
    bot.run()
//    val bot = TestBot
//    bot.run()
  }
}
