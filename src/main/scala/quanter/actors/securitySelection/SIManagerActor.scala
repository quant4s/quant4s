/**
  *
  */
package quanter.actors.securitySelection

import akka.actor.{Actor, ActorLogging, Props}
import quanter.actors.SecuritySelection
import quanter.securitySelection.Selector

/**
  *
  */
class SIManagerActor extends Actor with ActorLogging {
  var selector: Selector = null

  override def receive: Receive = {
    case s: SecuritySelection => {
      // 创建一个选股解释器
      val ref = context.actorOf(SelectionInterpreterActor.props(s.cmds, selector))
    }
  }

  def _loadSelector() = {
    selector = null
  }
}

object SIManagerActor {
  def props = {
    Props.create(classOf[SIManagerActor])
  }
  def path = "sif"
}
