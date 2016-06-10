package quanter.rest

import akka.actor.{Actor, ActorLogging  , Props}
import spray.routing.RejectionHandler.Default


/**
  *
  */
class HttpServer extends Actor with StrategyService with OrderService with TradeService with ActorLogging {
  def actorRefFactory = context
  implicit def executionContext = actorRefFactory.dispatcher
  def receive = runRoute(strategyServiceRoute ~ orderServiceRoute)
}

object HttpServer {
  def props(): Props = {
    Props(classOf[HttpServer])
  }
}
