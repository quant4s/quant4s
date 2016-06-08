package quanter.rest

import akka.actor.{Actor, ActorLogging, ActorRefFactory, Props}
import spray.http.{MediaTypes, StatusCodes}
import spray.routing._
import spray.routing.RejectionHandler.Default
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import quanter.domain.Strategy


/**
  *
  */
class HttpServer extends Actor with StrategyService with OrderService with ActorLogging {
  def actorRefFactory = context
  implicit def executionContext = actorRefFactory.dispatcher
  def receive = runRoute(strategyServiceRoute ~ orderServiceRoute)
}

object HttpServer {
  def props(): Props = {
    Props(classOf[HttpServer])
  }
}
