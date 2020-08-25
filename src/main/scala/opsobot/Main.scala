package opsobot

import opsobot.bot.Bot

object Main {
  def main(args: Array[String]): Unit = {
    val bot = Bot
    bot.run()
//    val bot = TestBot
//    bot.run()
  }
}
