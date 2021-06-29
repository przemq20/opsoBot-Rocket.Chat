package opsobot

import opsobot.router.Router
import opsobot.streambot.StreamBot

object Main {
  def main(args: Array[String]): Unit = {
    new Thread{
      override def run(): Unit = {
        val streamBot = new StreamBot
        streamBot.run()
      }
    }.start()
    new Thread{
      override def run(): Unit = new Router().run()
    }.start()
  }
}
