package opsobot

import opsobot.streambot.StreamBot

object Main {
  def main(args: Array[String]): Unit = {
    val streamBot = new StreamBot
    streamBot.run()
  }
}
