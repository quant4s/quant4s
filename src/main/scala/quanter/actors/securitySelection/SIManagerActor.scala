/**
  *
  */
package quanter.actors.securitySelection

import akka.actor.{Actor, ActorLogging}
import quanter.actors.SecuritySelection

/**
  *
  */
class SIManagerActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case s: SecuritySelection => {
      // 创建一个选股解释器
      val ref = context.actorOf(SelectionInterpreterActor.props(s.cmds))
    }
  }
}

object SIManagerActor {
  def path = "sif"
}
