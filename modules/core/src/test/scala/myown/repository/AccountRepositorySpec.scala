package myown
package repository

import models._

class AccountRepositorySpec extends UnitDBSpec with AccountRepository {
  "A AccountRepository" - {
    "query check" - {
      "queryByUserId" in {
        check(queryByUserId(1))
      }
      "queryById" in {
        check(queryById(1))
      }
      "queryCreate" in {
        check(queryCreate(1, "title"))
      }
      "queryUpsertBalance" in {
        check(queryUpsertBalance(1, Currency.USD))
      }
      "queryDiffBalance" in {
        check(queryDiffBalance(1, Money(10, Currency.USD)))
      }
    }
  }
}
