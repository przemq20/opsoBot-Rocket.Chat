package opsobot.streambot

import opsobot.parsers.Menu

abstract class MenuMessage(val content: Menu)
case class PizzadayMenuMessage(menu: Menu) extends MenuMessage(menu)
case class NoPizzadayMenuMessage(menu: Menu) extends MenuMessage(menu)
