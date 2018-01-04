package myown
package models

import scala.math.BigDecimal.RoundingMode

case class Money(value: BigDecimal, currency: Currency) {
  override def toString: String = currency.symbols.head + " " + value
  def raw: BigDecimal = BigDecimal((value * math.pow(10, currency.decimals)).toBigInt())
}

object Money {
  def unsafe(value: BigDecimal, currency: Currency): Money =
    Money((value / math.pow(10, currency.decimals)).setScale(currency.decimals, RoundingMode.DOWN), currency)
}
