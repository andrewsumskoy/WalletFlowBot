package myown
package models

object User {
  type Id = Long
  type TelegramId = Long
}

trait User {
  def id: User.Id
}

case class User0(
  id: User.Id,
  tgId: User.TelegramId,
  firstName: String,
  lastName: Option[String],
  username: Option[String],
  languageCode: LanguageCode,
  createAt: TS,
  lastSeenAt: TS
) extends User