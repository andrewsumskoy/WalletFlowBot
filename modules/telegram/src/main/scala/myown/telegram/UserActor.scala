package myown
package telegram

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import info.mukel.telegrambot4s.api.RequestHandler
import info.mukel.telegrambot4s.api.declarative.Commands
import models._

import scala.concurrent.ExecutionContext

class UserActor(val token: String, val client: RequestHandler)(tgId: User.TelegramId) extends Actor with Commands with LazyLogging {
  override implicit val executionContext: ExecutionContext = context.dispatcher

  def receive: Receive = {
    case _ =>
  }

}

object UserActor {
  def props(token: String, client: RequestHandler)(tgId: User.TelegramId): Props = Props(new UserActor(token, client)(tgId))
}
