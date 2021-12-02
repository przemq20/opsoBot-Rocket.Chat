package opsobot.parsers

import org.jsoup.Jsoup
import org.jsoup.select.Elements

import java.time.LocalDate
import scala.jdk.CollectionConverters._

object OpsoParser extends Parser {
  final val MENU_URL = "https://opso.pl/menu/"

  def parse(): Menu = {
    try {
      val document = Jsoup
        .connect(MENU_URL)
        .timeout(100000)
        .ignoreHttpErrors(true)
        .get()
      val menu = new Menu()

      val menuSection: Elements = document.select(".zestawy-obiadowe")
      val headers     = menuSection.select("h4")
      val currentDate = java.time.LocalDate.now

      val opsoDateText = headers.first.text.split(" ").drop(1).head.split("\\.").map(_.toInt)
      val opsoDate     = LocalDate.of(opsoDateText(2), opsoDateText(1), opsoDateText(0))

      if (opsoDate != currentDate) {
        scribe.error("Opso menu is not up to date")
        //throw NoUpdatedMenuException("Opso menu is not up to date")
        return menu
      }

      val dateHeader = headers.first
      headers.remove(dateHeader)

      headers.forEach(header => {
        val dishType = header.text
        val dishes = header
          .nextElementSibling()
          .select("p")
          .select(":not(.priceelement)")
          .eachText()
          .asScala
          .toList

        menu.addCategory(dishType, dishes)
      })
      menu
    } catch {
      case e: Throwable => new Menu
    }
  }
}
