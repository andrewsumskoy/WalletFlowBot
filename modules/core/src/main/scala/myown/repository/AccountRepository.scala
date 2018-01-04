package myown
package repository

import doobie._, doobie.implicits._, doobie.postgres.implicits._
import models._
import implicits._

trait AccountRepository {

  def queryByUserId(userId: User.Id): Query0[Account0] =
    sql"""
          SELECT a.id, a.user_id, a.title, COALESCE(JSON_AGG(b) FILTER (WHERE b.currency_code IS NOT NULL), '[]') as balance, a.create_at FROM accounts a
          LEFT JOIN account_balances b ON b.account_id = a.id
          WHERE a.user_id=$userId
          GROUP BY a.id, a.user_id, a.title, a.create_at""".query[Account0]

  def queryById(id: Account.Id): Query0[Account0] =
    sql"""
          SELECT a.id, a.user_id, a.title, COALESCE(JSON_AGG(b) FILTER (WHERE b.currency_code IS NOT NULL), '[]') as balance, a.create_at FROM accounts a
          LEFT JOIN account_balances b ON b.account_id = a.id
          WHERE a.id=$id
          GROUP BY a.id, a.user_id, a.title, a.create_at""".query[Account0]

  def queryCreate(userId: User.Id, title: String): Update0 =
    sql"INSERT INTO accounts(user_id, title) VALUES ($userId, $title)".update

  def queryUpsertBalance(accountId: Account.Id, currency: Currency): Update0 =
    sql"INSERT INTO account_balances(account_id, currency_code, balance) VALUES ($accountId, $currency, 0) ON CONFLICT DO NOTHING".update

  def queryDiffBalance(accountId: Account.Id, amount: Money): Update0 =
    sql"UPDATE account_balances SET balance = balance + ${amount.raw} WHERE account_id=$accountId AND currency_code=${amount.currency}".update

}
