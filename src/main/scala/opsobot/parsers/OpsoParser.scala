package opsobot.parsers

import org.jsoup.Jsoup
import org.jsoup.select.Elements

import scala.jdk.CollectionConverters._

object OpsoParser extends Parser {
  final val MENU_URL = "https://opso.pl/menu/"

  def parse(): Menu = {
    val document = Jsoup
      .connect(MENU_URL)
      .timeout(100000)
      .ignoreHttpErrors(true)
      .get()
    val menu = new Menu()

    val menuSection: Elements = document.select(".zestawy-obiadowe")
    val headers = menuSection.select("h4")

    val opsoDateText  = headers.first.text
    val opsoDateRegex = ".*(\\d{2})\\.(\\d{2})\\.(\\d{4})".r
    val opsoDate = opsoDateText match {
      case opsoDateRegex(day, month, year) =>
        java.time.LocalDate.of(year.toInt, month.toInt, day.toInt)
      case _ => throw new IllegalArgumentException("Opso date is not recognized")
    }
    val currentDate = java.time.LocalDate.now
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
  }
}
