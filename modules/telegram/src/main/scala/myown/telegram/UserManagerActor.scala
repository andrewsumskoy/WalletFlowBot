package myown
package telegram

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable, Props}
import info.mukel.telegrambot4s.api.BotBase
import info.mukel.telegrambot4s.models.Message

import scala.concurrent.duration._
import scala.collection.mutable

class UserManagerActor(bot: BotBase) extends Actor with ActorLogging {
  import context._

  val users = mutable.HashMap.empty[Int, (Long, ActorRef)]

  val cleaner: Cancellable = system.scheduler.schedule(1.minute, 1.minute, self, 'clean)

  def receive: Receive = {
    case m: Message => m.from.foreach(u => getActor(u.id).forward(m))
    case 'clean =>
      val now = System.currentTimeMillis()
      users.filter(el => now - el._2._1 >= 60000).foreach { case (id, (_, ref)) =>
        context.stop(ref)
          users -= id
      }
  }

  def getActor(id: Int): ActorRef = {
    val (t, ref) = users.getOrElseUpdate(id, System.currentTimeMillis() -> context.actorOf(UserActor.props(bot.token, bot.client)(id), s"user-$id"))
    users += id -> (System.currentTimeMillis() -> ref)
    ref
  }

  override def postStop(): Unit = {
    cleaner.cancel()
    super.postStop()
  }
}

object UserManagerActor {
  def props(bot: BotBase): Props = Props(new UserManagerActor(bot))
}
