package opsobot.parsers

import java.util.{ Calendar, TimeZone }

object MakarunParser extends Parser {
  def parse(): Menu = {
    val category = "Dzisiejsza Promocja"
    val menu     = new Menu
    menu.addCategory(category)
    val todayPromo = Calendar.getInstance(TimeZone.getTimeZone("GMT+2")).get(Calendar.DAY_OF_WEEK) match {
      case Calendar.MONDAY    => "Klasyczne poniedziałki - duży makaron Bolognese lub Carbonara w cenie małego."
      case Calendar.TUESDAY   => "Towarzyskie wtorki - zamów duży makaron, a drugi dostaniesz za pół ceny."
      case Calendar.WEDNESDAY => "Spragnione środy - do każdego zamówienia napój z rabatem -50%."
      case Calendar.THURSDAY  => "Indyjskie czwartki - wszystkie dania kuchni indyjskiej w cenie 15zł."
      case Calendar.FRIDAY    => "Wege piątek - dania wegetariańskie 15% taniej."
      case _                  => "Dzisiaj jest weekend"
    }
    menu.addToCategory(category, List(todayPromo))
    menu
  }
}
