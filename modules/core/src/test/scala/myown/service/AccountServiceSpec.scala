package myown
package service

import cats._, cats.implicits._
import doobie._, doobie.implicits._
import models._, repository._

class AccountServiceSpec extends UnitDBSpec with UserRepository {
  val srv = new AccountService0

  lazy val userId: Long = {
    rawSQL("DELETE FROM users")
    queryInsert(1, "", None, None, LanguageCode.en).withUniqueGeneratedKeys[Long]("id").transact(transactor).unsafeRunSync()
  }

  "A AccountService" - {
    "create account and get it" in {
      val acc = srv.create(userId, "acc1").transact(transactor).unsafeRunSync()
      val acc2 = srv.create(userId, "acc2").transact(transactor).unsafeRunSync()
      val accs = srv.byUserId(userId).transact(transactor).unsafeRunSync()

      accs should contain only (acc, acc2)
      acc.balance shouldBe Map.empty
      acc.title shouldBe "acc1"
    }

    "make deposit and update balance value" in {
      val q =
        srv.create(userId, "acc1")
          .flatMap(acc => srv.makeTransaction(Transaction.MakeDeposit(acc.id, Money(100, Currency.USD))))

      val (tr, acc) = q.transact(transactor).unsafeRunSync()
      acc.balance shouldBe Map(Currency.USD -> Money(100, Currency.USD))
      tr shouldBe a[Transaction.Deposit]
      inside(tr) { case dep: Transaction.Deposit =>
        dep.to shouldBe acc.id
        dep.amount shouldBe Money(100, Currency.USD)
      }
    }

    "make payment and update balance value" in {
      val q =
        srv.create(userId, "acc1")
          .flatMap(acc => srv.makeTransaction(Transaction.MakePayment(acc.id, Money(100, Currency.USD))))

      val (tr, acc) = q.transact(transactor).unsafeRunSync()
      acc.balance shouldBe Map(Currency.USD -> Money(-100, Currency.USD))
      tr shouldBe a[Transaction.Payment]
      inside(tr) { case dep: Transaction.Payment =>
        dep.from shouldBe acc.id
        dep.amount shouldBe Money(100, Currency.USD)
      }
    }

    "make transfer and update balance value" in {
      val q =
        for {
          from <- srv.create(userId, "from")
          to <- srv.create(userId, "to")
          out <- srv.makeTransaction(Transaction.MakeTransfer(from.id, to.id, Money(100, Currency.USD)))
          toUpdated <- srv.queryById(to.id).unique
        } yield (out._2, toUpdated, out._1)

      val (from, to, tr) = q.transact(transactor).unsafeRunSync()
      from.balance shouldBe Map(Currency.USD -> Money(-100, Currency.USD))
      to.balance shouldBe Map(Currency.USD -> Money(100, Currency.USD))
      tr shouldBe a[Transaction.Transfer]
      inside(tr) { case dep: Transaction.Transfer =>
        dep.from shouldBe from.id
        dep.to shouldBe to.id
        dep.amount shouldBe Money(100, Currency.USD)
      }
    }

    "make alignment and update balance value" in {
      val q =
        for {
          acc <- srv.create(userId, "acc1")
          _ <- srv.makeTransaction(Transaction.MakeDeposit(acc.id, Money(100, Currency.USD)))
          ret <- srv.makeTransaction(Transaction.MakeAdjustment(acc.id, Money(-30, Currency.USD)))
        } yield ret

      val (tr, acc) = q.transact(transactor).unsafeRunSync()
      acc.balance shouldBe Map(Currency.USD -> Money(70, Currency.USD))
      tr shouldBe a[Transaction.Adjustment]
      inside(tr) { case dep: Transaction.Adjustment =>
        dep.account shouldBe acc.id
        dep.amount shouldBe Money(-30, Currency.USD)
      }
    }
  }

  override def beforeEach(): Unit = {
    rawSQL("DELETE FROM transactions")
    rawSQL("DELETE FROM account_balances")
    rawSQL("DELETE FROM accounts")
  }
}
