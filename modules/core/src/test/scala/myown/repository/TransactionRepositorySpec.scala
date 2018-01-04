package myown
package repository

import models._

class TransactionRepositorySpec extends UnitDBSpec with TransactionRepository {
  "A TransactionRepository" - {
    "query check" - {
      "queryCreateTransaction" in {
        check(queryCreateTransaction(TransactionType.Deposit, None, Some(1), Money(10d, Currency.USD)))
      }

      "queryTransactionById" in {
        check(queryTransactionById(1))
      }
    }
  }
}
