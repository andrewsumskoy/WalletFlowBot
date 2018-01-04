package myown
package service

import doobie._
import models._
import repository._

trait UserService {
  def createIfNeed(tgId: User.TelegramId,
                   firstName: String,
                   lastName: Option[String],
                   username: Option[String],
                   languageCode: LanguageCode): ConnectionIO[User0]
}

class UserService0 extends UserService with UserRepository {
  def createIfNeed(tgId: User.TelegramId,
                   firstName: String,
                   lastName: Option[String],
                   username: Option[String],
                   languageCode: LanguageCode): ConnectionIO[User0] =
    for {
      find <- queryByTgIdForUpdate(tgId).option
      ret <- find match {
        case Some(u) => queryUpdateLastSeen(u.id).withUniqueGeneratedKeys[TS]("last_seen_at").map(dt => u.copy(lastSeenAt = dt))
        case _ =>
          for {
            id <- queryInsert(tgId, firstName, lastName, username, languageCode).withUniqueGeneratedKeys[Long]("id")
            created <- queryById(id).unique
          } yield created
      }
    } yield ret
}
