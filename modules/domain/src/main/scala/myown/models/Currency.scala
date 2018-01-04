package myown
package models

import enumeratum._

sealed abstract class Currency(override val entryName: String, val title: String, val symbols: Seq[String], val decimals: Int) extends EnumEntry

object Currency extends Enum[Currency] with CirceEnum[Currency] {

  case object USD extends Currency("USD", "US Dollar", "$" :: Nil, 2)
  case object EUR extends Currency("EUR", "Euro", "€" :: Nil, 2)
  case object RUB extends Currency("RUB", "Russian Ruble", "\u20BD" :: "руб" :: "р" :: Nil, 2)

  val values = findValues

  def withSymbolOption(s: String): Option[Currency] = values.find(_.symbols.contains(s))

  def withSymbol(s: String): Currency =
    withSymbolOption(s).getOrElse(throw new NoSuchElementException(s"$s is not a member of Enum (Currency)"))
}
