package myown.telegram

import pureconfig.loadConfigOrThrow

object config {
  final case class Bot(token: String)
  final case class DBPool(min: Int, max: Int)
  final case class DB(url: String, username: String, password: String, migrate: Boolean, pool: DBPool)

  lazy val bot: Bot = loadConfigOrThrow[Bot]("myown.bot")
  lazy val db: DB = loadConfigOrThrow[DB]("myown.db")
}
