package opsobot.parsers

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

import javax.net.ssl.{ HostnameVerifier, SSLSession }

object HindusParser extends App with Parser {
  final val MENU_URL = "https://www.facebook.com/HindusFood-Krak%C3%B3w-MENU-110617490850262"

  def parse(): Menu = {
    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
      new HostnameVerifier {
        override def verify(hostname: String, session: SSLSession): Boolean =
          hostname.equals("www.facebook.com"); // or return true
      }
    )

    val document: Document = Jsoup.connect(MENU_URL).get()
    println(document.select("rq0escxv l9j0dhe7 du4w35lb qmfd67dx hpfvmrgz gile2uim buofh1pr g5gj957u aov4n071 oi9244e8 bi6gxh9e h676nmdw aghb5jc5"))
    val menu = new Menu()

    val dishTypeBlocks: Elements = document.select(".menu-category-block")
    if (dishTypeBlocks.isEmpty) {
      //      throw NoUpdatedMenuException("Olimp menu is unavailable")
      scribe.error("Olimp menu is unavailable")
      return menu
    }
    dishTypeBlocks.forEach(block => {
      val dishType = block.select("h3").text

      val spanTagRegex = "(.*)<span>(.*)</span>".r
      val parsedDishType = dishType match {
        case spanTagRegex(a, b) => a + b
        case _                  => dishType
      }

      menu.addCategory(parsedDishType)

      val dishes = block
        .select(".menu-dishes")
        .text()
        .split(',')
        .map(_.trim)
        .toList

      menu.addToCategory(parsedDishType, dishes)
    })
    menu
  }
  println(parse())
}
