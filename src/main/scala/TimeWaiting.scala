import scala.concurrent.Future
import java.time.temporal.ChronoUnit
import java.time.{Duration, LocalDate, LocalDateTime, LocalTime, Period}

import akka.actor.ActorSystem

object TimeWaiting extends App {
  import system.dispatcher
  implicit val system: ActorSystem = ActorSystem("RocketChat")

  var destinationTime = LocalTime.of(13, 18, 0)

  Future {
    while (true) {
      val currentTime = LocalTime.now.truncatedTo(ChronoUnit.SECONDS)

      val timeLeft = Duration.between(currentTime, destinationTime)
      val remainingMillis = timeLeft.getSeconds * 1000

      println(s"Milisekund: $remainingMillis, sekund: ${remainingMillis/1000}")
      if (remainingMillis > 0) {
        if (remainingMillis < 10000) {
          println("sleep for 1000")
          Thread.sleep(1000)
        } else {
          println(s"sleep for ${remainingMillis / 2}")
          Thread.sleep(remainingMillis / 2)
        }
      } else {
        println(s"${"="*20}Wysyłanie menu")
        val twentyThreeHoursInMillis = 23 * 60 * 60 * 1000
        println(s"Sleep for: ${twentyThreeHoursInMillis / 1000} seconds")
        Thread.sleep(twentyThreeHoursInMillis)
      }
    }
  }
}
