package quanter.rest

import akka.actor.{Actor, ActorLogging  , Props}
import spray.http.HttpHeaders.RawHeader
import spray.routing.RejectionHandler.Default


/**
  *
  */
class HttpServer extends Actor with StrategyService with OrderService with TradeAccountService with DataService with ActorLogging {
  def actorRefFactory = context
  implicit def executionContext = actorRefFactory.dispatcher
  def receive = runRoute( respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")){
    strategyServiceRoute ~ orderServiceRoute ~ tradeAccountServiceRoute ~ dataServiceRoute
  })
}

object HttpServer {
  def props(): Props = {
    Props(classOf[HttpServer])
  }
}
