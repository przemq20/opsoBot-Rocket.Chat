package opsobot

import opsobot.streambot.StreamBot

object RunStream {
  def main(args: Array[String]): Unit = {
    val streamBot = new StreamBot
    streamBot.run()
  }
}
