package quanter.rest

import akka.pattern.ask
import akka.util.Timeout
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Extraction, Formats}
import quanter.actors.trade.{ListTraders, TradeRouteActor}
import spray.routing.HttpService

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

/**
  *
  */
trait TradeService extends HttpService{
  val tradeServiceRoute = {
    get {
      path("trade" / "list") {
        complete {
          _getAllTraders()
        }
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
      path("trade" / "connect" / IntNumber) {
        id => {
          complete {
            _reconnect(id)
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

  val traderManager = actorRefFactory.actorSelection("/user/" + TradeRouteActor.path)
  private def _getAllTraders(): String = {
    implicit val timeout = Timeout(5 seconds)
    val future = traderManager ? ListTraders
    val result = Await.result(future, timeout.duration).asInstanceOf[Array[Trader]]
    implicit val formats: Formats = DefaultFormats
    val json = Extraction.decompose(result)
    compact(render(json))
  }

  private def _reconnect(id: Int): String = {

    """{"code": 1}"""
  }
}
