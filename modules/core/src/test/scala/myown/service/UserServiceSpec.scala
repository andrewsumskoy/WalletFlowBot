package myown
package service

import cats._, cats.implicits._
import doobie._, doobie.implicits._
import models._

class UserServiceSpec extends UnitDBSpec {
  val srv = new UserService0
  "A UserService" - {
    "must create user if not exists and update last seen date if exists" in {
      sql"SELECT count(*) FROm users".query[Long].unique.transact(transactor).unsafeRunSync() shouldBe 0

      val usr = srv.createIfNeed(1, "firstName", None, None, LanguageCode.en).transact(transactor).unsafeRunSync()
      val usr2 = srv.createIfNeed(1, "firstName", Some("sec"), None, LanguageCode.en).transact(transactor).unsafeRunSync()
      usr shouldBe usr2.copy(lastSeenAt = usr.lastSeenAt)
      usr2.lastSeenAt should be > usr.lastSeenAt
    }
  }

  override def beforeEach(): Unit = {
    rawSQL("DELETE FROM transactions")
    rawSQL("DELETE FROM account_balances")
    rawSQL("DELETE FROM accounts")
    rawSQL("DELETE FROM users")
  }
}
