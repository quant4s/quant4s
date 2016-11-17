/**
  *
  */
package quanter.actors.scheduling

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}

/**
  *
  */
trait BaseJob extends Actor with ActorLogging{
  override def receive: Receive = {
    case exec: ExecuteJob => executeJob()
  }

  def executeJob(): Unit
}
