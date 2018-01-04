package myown
package repository

import models.LanguageCode

class UserRepositorySpec extends UnitDBSpec with UserRepository{
  "A UserRepository" - {
    "query check" - {
      "queryByTgIdForUpdate" in {
        check(queryByTgIdForUpdate(0))
      }

      "queryById" in {
        check(queryById(0))
      }

      "queryInsert" in {
        check(queryInsert(0, "fs", None, Some("ln"), LanguageCode.ru))
      }

      "queryUpdateLastSeen" in {
        check(queryUpdateLastSeen(1))
      }
    }
  }
}
