package opsobot.parsers
import scala.reflect.runtime.universe._

sealed trait Category {
  val name: String
}
object Category {
  case object Sniadania extends Category {
    val name = "Śniadania"
  }
  case object Zupy extends Category {
    val name = "Zupy"
  }
  case object Danie_miesne_lub_rybne extends Category {
    val name = "Danie mięsne lub rybne"
  }
  case object Drugie_danie_miesne_lub_rybne extends Category {
    val name = "Drugie danie mięsne lub rybne"
  }
  case object Dania_z_miesem extends Category {
    val name = "Dania z mięsem"
  }
  case object Dania_z_ryb extends Category {
    val name = "Dania z ryb"
  }
  case object Dania_wegetarianskie extends Category {
    val name = "Dania Wegetariańskie"
  }
  case object Dania_weganskie extends Category {
    val name = "Dania Wegańskie"
  }
  case object Pizza extends Category {
    val name = "Pizza"
  }
  case object Dodatki extends Category {
    val name = "Dodatki"
  }
  case object Salatka_lub_surowka extends Category {
    val name = "Sałatka lub surówka"
  }
  case object Desery extends Category {
    val name = "Desery"
  }
  case object Napoje extends Category {
    val name = "Napoje"
  }

  val allCategories: List[String] = List(
    Sniadania,
    Zupy,
    Danie_miesne_lub_rybne,
    Drugie_danie_miesne_lub_rybne,
    Dania_z_miesem,
    Dania_z_ryb,
    Dania_wegetarianskie,
    Dania_weganskie,
    Pizza,
    Dodatki,
    Salatka_lub_surowka,
    Desery,
    Napoje
  ).map(_.name)
}
