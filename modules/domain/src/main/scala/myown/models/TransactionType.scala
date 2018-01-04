package myown
package models

import enumeratum.EnumEntry.Lowercase
import enumeratum._

sealed trait TransactionType extends EnumEntry

object TransactionType extends Enum[TransactionType] {

  case object Adjustment extends TransactionType with Lowercase
  case object Transfer extends TransactionType with Lowercase
  case object Deposit extends TransactionType with Lowercase
  case object Payment extends TransactionType with Lowercase

  val values = findValues
}
