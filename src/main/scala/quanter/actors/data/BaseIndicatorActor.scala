/**
  *
  */
package quanter.actors.data

import akka.actor.{Actor, ActorLogging}
import quanter.actors.securities.SecuritiesManagerActor
import quanter.actors.zeromq.ZeroMQServerActor

/**
  *
  */
abstract class BaseIndicatorActor extends Actor with ActorLogging {
  val securitiesManagerRef = context.actorSelection("/user/" + SecuritiesManagerActor.path)
  val pubRef = context.actorSelection("/user/" + ZeroMQServerActor.path)

}
