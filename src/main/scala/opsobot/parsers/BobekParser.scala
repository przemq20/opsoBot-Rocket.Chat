package opsobot.parsers

object BobekParser extends Parser {
  def parse(): Menu = {
    val menu     = new Menu
    menu.addCategory("Menu", List("https://www.facebook.com/BobekBurger/photos/p.102639172583046/102639172583046"))
    menu
  }
}
