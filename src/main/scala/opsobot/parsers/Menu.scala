package opsobot.parsers

import scala.collection.mutable

class Menu() {
  private val sortingOrder: List[String] = List[String]("Śniadania", "Zupy", "Danie mięsne lub rybne", "Dania z mięsem", "Dania z ryb", "Dania wegetariańskie",
    "Dania wegańskie", "Pizza", "Dodatki", "Sałatka lub surówka", "Desery", "Napoje")

  private val data: mutable.Map[String, List[String]] = mutable.Map[String, List[String]]()

  def addCategory(category: String): Menu = {
    addCategory(category, List.empty)
    this
  }

  def addCategory(category: String, dishes: List[String]): Menu = {
    data.update(category, dishes)
    this
  }

  def addToCategory(category: String, dishes: List[String]): Menu = {
    val existingDishes = data.getOrElseUpdate(category, dishes)
    if (existingDishes.isEmpty) data.update(category, dishes)
    this
  }

  def categories(): Iterable[String] = data.keys

  def dishes(category: String): List[String] = {
    data.getOrElse(category, List.empty)
  }

  override def toString: String = {
    val builder = new StringBuilder()
    if (data.isEmpty) {
      "Menu na dzisiaj jest niedostępne"
    }
    else {
      val sorted = this.sort()
      sorted.foreach(category => {
        val categoryName = category._1
        val dishesList = category._2
        builder.addAll(categoryName)
        builder.addAll(":")
        dishesList.foreach(dish => {
          builder.addAll("\n\t- ")
          builder.addAll(dish)
        })
        builder.addAll("\n")
      })
      builder.result()
    }
  }

  def sort(): Seq[(String, List[String])] = {
    data.toSeq.sortWith((a, b) => sortingOrder.indexOf(a._1) < sortingOrder.indexOf(b._1))
  }

}
