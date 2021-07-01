package opsobot

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._

object Main {
  def main(args: Array[String]):Unit = {
        new Thread{
          override def run(): Unit = {
            val streamBot = new StreamBot
            streamBot.run()
          }
        }.start()
    //    new Thread{
    //      override def run(): Unit = new Router().run()
    //    }.start()

    val route = get {
      complete(
        HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          "hello"
        )
      )
    }
    implicit val system = ActorSystem("Server")

    val host = "0.0.0.0"
    val port: Int = sys.env.getOrElse("PORT", "8080").toInt

    Http().bindAndHandle(route, host, port)
  }
}
