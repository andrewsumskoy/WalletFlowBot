package myown.telegram

import akka.actor.ActorSystem
import com.typesafe.scalalogging.LazyLogging
import myown.migration.Migration

import scala.util.control.NonFatal

object TelegramBoot extends App with LazyLogging {
  implicit val system: ActorSystem = ActorSystem("myown")
  implicit val ec = system.dispatcher
  system.whenTerminated.foreach(_ => db.ds.close())
  try {
    val bot = new TelegramBot()
    if (config.db.migrate) Migration.migrate(config.db.url, config.db.username, config.db.password)
    bot.run()
  } catch {
    case NonFatal(e) =>
      logger.error("Fail on startup", e)
      system.terminate()
  }
}
