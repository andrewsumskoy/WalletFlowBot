package myown
package models

object Account {
  type Id = Long
}

trait Account {
  def id: Account.Id
  def userId: User.Id
  def balance: Map[Currency, Money]
}

case class Account0(
  id: Account.Id,
  userId: User.Id,
  title: String,
  balance: Map[Currency, Money],
  createAt: TS
) extends Account
