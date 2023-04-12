package opsobot.parsers

import org.apache.tika.metadata.Metadata
import org.apache.tika.parser.ParseContext
import org.apache.tika.parser.pdf.PDFParser
import org.apache.tika.sax.BodyContentHandler
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.io.{File, FileInputStream, FileOutputStream}
import java.time.{LocalDate, ZoneId}

object OpsoParserPDF extends Parser {
  val MENU_URL = "https://opso.pl/"
  val FILEPATH = "file.pdf"

  private def getMenuLink: String = {
    val opsoPage: Document = Jsoup
      .connect(MENU_URL)
      .timeout(100000)
      .ignoreHttpErrors(true)
      .get()

    val buttons    = opsoPage.getElementsByAttributeValue("role", "button")
    val menuButton = buttons.get(0)
    val menuLink   = menuButton.attributes().get("href")
    menuLink
  }

  private def downloadMenuPdf(link: String): File = {
    val menuPdf = Jsoup
      .connect(link)
      .timeout(100000)
      .ignoreContentType(true)
      .ignoreHttpErrors(true)
      .execute()
      .bodyAsBytes()

    val file = new File(FILEPATH)
    val os   = new FileOutputStream(file)
    os.write(menuPdf)
    os.close()

    file
  }

  private def parsePdf(pdf: File): BodyContentHandler = {
    val fStream        = new FileInputStream(pdf)
    val contentHandler = new BodyContentHandler()
    val pdfParser      = new PDFParser()
    val metadata       = new Metadata
    val parseContext   = new ParseContext

    pdfParser.parse(fStream, contentHandler, metadata, parseContext)

    contentHandler
  }

  def parse(): Menu = {
    val link           = getMenuLink
    val pdf            = downloadMenuPdf(link)
    val contentHandler = parsePdf(pdf)

    val menuItems = contentHandler.toString
      .split("\n")
      .map(_.trim)
      .filter(_.nonEmpty)
      .filterNot(_.startsWith("("))
      .toList

    val currentDate  = java.time.LocalDate.now
    val opsoDateText = menuItems.head.split("\\.")
    val day   = opsoDateText(0).filter(_.isDigit).trim.toInt
    val month = opsoDateText(1).filter(_.isDigit).trim.toInt
    val year  = opsoDateText(2).filter(_.isDigit).trim.toInt
    val opsoDate = LocalDate.of(year, month, day)
    if (opsoDate != currentDate) {
      new Menu
    } else {
      val menuDishesAndCategories = menuItems.drop(1)
      val allCategories           = Category.allCategories
      val categories              = menuDishesAndCategories.filter(a => allCategories.map(_.toLowerCase()).contains(a.toLowerCase()))
      val positionsOfCategories = categories.map(menuDishesAndCategories.indexOf(_)) ++ List(
        menuDishesAndCategories.length
      )

      val dishesList = for { category <- categories.indices } yield {
        val position1 = positionsOfCategories(category)
        val position2 = positionsOfCategories(category + 1)

        menuDishesAndCategories.slice(position1, position2)
      }

      val menu = new Menu
      dishesList.foreach(dishes => menu.addCategory(dishes.head, dishes.drop(1)))

      menu
    }
  }
}
