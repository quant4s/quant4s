/**
  *
  */
package quanter.actors.data

import akka.actor.{Actor, ActorLogging}
import quanter.actors.securities.SecuritiesManagerActor
import quanter.actors.zeromq.{ZeroMQSubPubServerActor, ZeroMQSubPubServerActor$}

/**
  *
  */
abstract class BaseIndicatorActor extends Actor with ActorLogging {
  val securitiesManagerRef = context.actorSelection("/user/" + SecuritiesManagerActor.path)
  val pubRef = context.actorSelection("/user/" + ZeroMQSubPubServerActor.path)

}
