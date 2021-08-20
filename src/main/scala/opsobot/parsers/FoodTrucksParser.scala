package opsobot.parsers

import java.util.{ Calendar, TimeZone }

object FoodTrucksParser {
  def parse: Menu = {
    val category = "Dzisiejsze Food Trucki"
    val menu     = new Menu
    menu.addCategory(category)
    val todayFoodTrucks = Calendar.getInstance(TimeZone.getTimeZone("GMT+2")).get(Calendar.DAY_OF_WEEK) match {
      case Calendar.MONDAY    => List("RAJSKIE TAJSKIE - Kuchnia tajska", "GRILL MOBIL- Kuchnia turecko-grecka")
      case Calendar.TUESDAY   => List("HINDUS INDIAN FOOD - kuchnia indyjska")
      case Calendar.WEDNESDAY => List("MANUFAKTURA KRAKOWSKA - burgery i frytki", "PIEROGOWE LOVE - pierogi")
      case Calendar.THURSDAY  => List("ROZBRYKANA OWCA - dania inspirowane kuchnią marokańską")
      case Calendar.FRIDAY    => List("COMPADRE - kuchnia meksykańska", "CHINA BAR - kuchnia wietnamska")
      case _                  => List("")
    }
    menu.addToCategory(category, todayFoodTrucks)
    menu
  }
}
