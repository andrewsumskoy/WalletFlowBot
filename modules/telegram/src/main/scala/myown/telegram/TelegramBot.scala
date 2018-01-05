package myown.telegram

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger
import info.mukel.telegrambot4s.api.{BotBase, Polling, RequestHandler}
import info.mukel.telegrambot4s.api.declarative.Commands
import info.mukel.telegrambot4s.clients.AkkaClient

import scala.concurrent.ExecutionContext

class TelegramBot()(implicit override val system: ActorSystem) extends BotBase with Polling with Commands {
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  protected val logger: Logger = Logger(getClass)
  val token: String = config.bot.token
  val client: RequestHandler = new AkkaClient(token)

  val manager: ActorRef = system.actorOf(UserManagerActor.props(this), "usermanager")
}
