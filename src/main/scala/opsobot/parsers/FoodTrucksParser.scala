package opsobot.parsers

import java.util.{ Calendar, TimeZone }

object FoodTrucksParser extends Parser {
  def parse(): Menu = {
    val menu = new Menu
    val todayFoodTrucks: List[(String, List[String])] =
      Calendar.getInstance(TimeZone.getTimeZone("GMT+2")).get(Calendar.DAY_OF_WEEK) match {
        case Calendar.MONDAY =>
          List(
//            ("RAJSKIE TAJSKIE - Kuchnia tajska", List.empty),
//            ("GRILL MOBIL- Kuchnia turecko-grecka", List("http://grillmobil.pl/to-ci-dopiero-menu/")),
            ("U Włocha - kuchnia włoska", List("https://www.facebook.com/people/U-Wlocha-Food-Truck/100057174591521/"))
          )
        case Calendar.TUESDAY =>
          List(
            (HindusParser.foodTruckName, HindusParser.parse().dishes(HindusParser.category))
          )
        case Calendar.WEDNESDAY =>
          List(
            (
              "MANUFAKTURA KRAKOWSKA - burgery i frytki",
              List("https://www.facebook.com/manufakturakrakowska/photos/a.1579173878964213/2668016340079956/")
            )
          )
        case Calendar.THURSDAY =>
          List(
            ("ROZBRYKANA OWCA - dania inspirowane kuchnią marokańską", List("https://rozbrykanaowca.pl/menu/")),
            ("PIEROGOWE LOVE - pierogi", List.empty)
          )
        case Calendar.FRIDAY =>
          List(
            ("COMPADRE - kuchnia meksykańska", List("https://www.facebook.com/compadrefoodtruck/menu")),
//            ("CHINA BAR - kuchnia wietnamska", List.empty)
          )
        case _ => List(("", List("")))
      }
    todayFoodTrucks.foreach(food => {
      menu.addCategory(food._1, food._2)
    })
    menu
  }
}
