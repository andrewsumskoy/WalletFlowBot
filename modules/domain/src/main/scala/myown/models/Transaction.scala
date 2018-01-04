package myown
package models

sealed trait Transaction {
  def id: Transaction.Id
  def amount: Money
  def at: TS
}

sealed trait MakeTransaction {
  def amount: Money
}

object Transaction {
  type Id = Long

  case class Adjustment(id: Transaction.Id, account: Account.Id, amount: Money, at: TS) extends Transaction
  case class Transfer(id: Transaction.Id, from: Account.Id, to: Account.Id, amount: Money, at: TS) extends Transaction
  case class Payment(id: Transaction.Id, from: Account.Id, amount: Money, at: TS) extends Transaction
  case class Deposit(id: Transaction.Id, to: Account.Id, amount: Money, at: TS) extends Transaction

  case class MakeAdjustment(account: Account.Id, amount: Money) extends MakeTransaction
  case class MakeTransfer(from: Account.Id, to: Account.Id, amount: Money) extends MakeTransaction
  case class MakePayment(from: Account.Id, amount: Money) extends MakeTransaction
  case class MakeDeposit(to: Account.Id, amount: Money) extends MakeTransaction
}
