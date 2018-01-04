package myown
package repository

import doobie._, doobie.implicits._
import models._
import implicits._

trait TransactionRepository {
  def queryCreateTransaction(transactionType: TransactionType, from: Option[Account.Id], to: Option[Account.Id], amount: Money): Update0 =
    sql"INSERT INTO transactions(transaction_type, from_account_id, to_account_id, currency_code, amount) VALUES ($transactionType, $from, $to, ${amount.currency}, ${amount.raw})".update

  def queryTransactionById(id: Transaction.Id): Query0[Transaction] =
    sql"SELECT id, transaction_type, from_account_id, to_account_id, currency_code, amount, create_at FROM transactions WHERE id=$id".query[Transaction]

}
