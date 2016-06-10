package quanter.rest

import akka.actor.{ActorRefFactory, ActorSelection}
import spray.routing.HttpService

/**
  *
  */
trait TradeService extends HttpService{
  val tradeServiceRoute = {
    get {
      path("trade" / "list") {
        complete("")
      }
    }~
    post {
      path("trade") {
        requestInstance {
          request => {
            complete("")
          }
        }
      }
    }~
    put {
      path("trade" / IntNumber) {
        id => {
          requestInstance {
            request => {
              complete("")
            }
          }
        }
      }
    }~
    delete {
      path("trade" / IntNumber) {
        id => {
          complete("")
        }
      }
    }
  }
}
