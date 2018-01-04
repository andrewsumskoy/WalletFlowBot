package myown
package models

import enumeratum._

sealed abstract class LanguageCode(val code: String) extends EnumEntry {
  override def toString: String = code
}

object LanguageCode extends Enum[LanguageCode] {

  case object en extends LanguageCode("en")
  case object enUS extends LanguageCode("en-us")
  case object ru extends LanguageCode("ru")
  case object ruRU extends LanguageCode("ru-ru")

  def custom(code: String): LanguageCode = new LanguageCode(code) {}

  val default: LanguageCode = en
  val values = findValues
}
