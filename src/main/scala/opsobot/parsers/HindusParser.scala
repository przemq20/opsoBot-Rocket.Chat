package opsobot.parsers

import org.jsoup.Jsoup
import play.api.libs.json.Json

object HindusParser extends Parser {
  final val MENU_URL = "https://hindus-api.herokuapp.com/hindus"
  val foodTruckName = "HINDUS INDIAN FOOD - kuchnia indyjska"
  val category      = ""

  def parse(): Menu = {
    val menu = new Menu()

    val connection = Jsoup
      .connect(MENU_URL)
      .timeout(100000)
      .ignoreHttpErrors(true)
      .ignoreContentType(true)
      .execute()
    val code = connection.statusCode()
    if (code != 200) {
      menu.addCategory(category, List.empty)
      return menu
    }
    val document = connection.parse()

    val json = Json.parse(document.body().text())

    val list = json.as[List[String]]
    menu.addCategory(category, list)
    menu
  }
}
