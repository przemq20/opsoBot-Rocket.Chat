package opsobot.parsers

import java.util.{ Calendar, TimeZone }

object MakarunParser extends Parser {
  def parse(): Menu = {
    val category = "Dzisiejsza Promocja"
    val menu     = new Menu
    menu.addCategory(category)
    val todayPromo = Calendar.getInstance(TimeZone.getTimeZone("GMT+2")).get(Calendar.DAY_OF_WEEK) match {
      case Calendar.MONDAY    => "Towarzyskie poniedziałki - zamów duży makaron, a drugi dostaniesz za pół ceny"
      case Calendar.TUESDAY   => "Studenckie wtorki - dla studentów wszystkie dania kuchni indyjskiej za 9.90 zł"
      case Calendar.WEDNESDAY => "Orzeźwiające środy - do każdego Makaruna i Hindusa napój z rabatem -50%."
      case Calendar.THURSDAY  => "Klasyczne czwartki - duży makaron Bolognese lub Currygodny w cenie małego"
      case Calendar.FRIDAY    => "Wege piątek - makarony wegetariańskie 15% taniej."
      case _                  => "Dzisiaj jest weekend"
    }
    menu.addToCategory(category, List(todayPromo))
    menu.addCategory("Pełne menu", List("https://makarun.pl/punkt/avia-offices-zyczkowskiego-20/"))
    menu
  }
}
