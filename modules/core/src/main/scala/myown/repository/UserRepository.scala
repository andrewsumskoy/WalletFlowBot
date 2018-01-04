package myown
package repository

import doobie._, doobie.implicits._
import models._
import implicits._

trait UserRepository {

  def queryByTgIdForUpdate(tgId: User.TelegramId): Query0[User0] =
    sql"SELECT id, telegram_id, first_name, last_name, username, language_code, create_at, last_seen_at FROM users WHERE telegram_id=$tgId FOR UPDATE".query[User0]

  def queryById(id: User.Id): Query0[User0] =
    sql"SELECT id, telegram_id, first_name, last_name, username, language_code, create_at, last_seen_at FROM users WHERE id=$id".query[User0]

  def queryInsert(tgId: User.TelegramId,
                  firstName: String,
                  lastName: Option[String],
                  username: Option[String],
                  languageCode: LanguageCode): Update0 =
    sql"INSERT INTO users(telegram_id, first_name, last_name, username, language_code) VALUES($tgId, $firstName, $lastName, $username, $languageCode)".update

  def queryUpdateLastSeen(id: User.Id): Update0 =
    sql"UPDATE users SET last_seen_at=now() WHERE id=$id".update
}
