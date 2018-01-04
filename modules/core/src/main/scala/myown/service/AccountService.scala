package myown
package service

import doobie._
import models._
import repository._

trait AccountService {
  def byUserId(userId: User.Id): ConnectionIO[List[Account0]]
  def create(userId: User.Id, title: String): ConnectionIO[Account0]

  def makeTransaction(t: MakeTransaction): ConnectionIO[(Transaction, Account0)]
}

class AccountService0 extends AccountService with AccountRepository with TransactionRepository {
  def byUserId(userId: User.Id): ConnectionIO[List[Account0]] = queryByUserId(userId).list

  def create(userId: User.Id, title: String): ConnectionIO[Account0] =
    for {
      id <- queryCreate(userId, title).withUniqueGeneratedKeys[Long]("id")
      ret <- queryById(id).unique
    } yield ret

  def makeTransaction(t: MakeTransaction): ConnectionIO[(Transaction, Account0)] = {
    val op = t match {
      case Transaction.MakeDeposit(to, amount) =>
        for {
          trId <- queryCreateTransaction(TransactionType.Deposit, None, Some(to), amount).withUniqueGeneratedKeys[Long]("id")
          _ <- queryUpsertBalance(to, amount.currency).run
          _ <- queryDiffBalance(to, amount).run
          tr <- queryTransactionById(trId).unique
        } yield (tr, to)
      case Transaction.MakeAdjustment(to, amount) =>
        for {
          trId <- queryCreateTransaction(TransactionType.Adjustment, None, Some(to), amount).withUniqueGeneratedKeys[Long]("id")
          _ <- queryUpsertBalance(to, amount.currency).run
          _ <- queryDiffBalance(to, amount).run
          tr <- queryTransactionById(trId).unique
        } yield (tr, to)
      case Transaction.MakePayment(from, amount) =>
        for {
          trId <- queryCreateTransaction(TransactionType.Payment, Some(from), None, amount).withUniqueGeneratedKeys[Long]("id")
          _ <- queryUpsertBalance(from, amount.currency).run
          _ <- queryDiffBalance(from, amount.copy(value = amount.value * -1)).run
          tr <- queryTransactionById(trId).unique
        } yield (tr, from)
      case Transaction.MakeTransfer(from, to, amount) =>
        for {
          trId <- queryCreateTransaction(TransactionType.Transfer, Some(from), Some(to), amount).withUniqueGeneratedKeys[Long]("id")
          _ <- queryUpsertBalance(from, amount.currency).run
          _ <- queryUpsertBalance(to, amount.currency).run
          _ <- queryDiffBalance(from, amount.copy(value = amount.value * -1)).run
          _ <- queryDiffBalance(to, amount).run
          tr <- queryTransactionById(trId).unique
        } yield (tr, from)
    }
    for {
      opResult <- op
      ret <- queryById(opResult._2).unique
    } yield (opResult._1, ret)
  }
}
