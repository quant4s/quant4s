package quanter.rest

import akka.pattern.ask
import akka.util.Timeout
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Extraction, Formats}
import quanter.actors.trade._
import quanter.actors._
import spray.routing.HttpService

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

/**
  *
  */
trait TradeAccountService extends HttpService{
  val tradeAccountServiceRoute = {
    get {
      path("account" / "list") {
        complete {
          _getAllTraders()
        }
      }
    } ~
    post {
      path("account") {
        requestInstance {
          request => {
            complete {
              _createTrader(request.entity.data.asString)
            }
          }
        }
      }
    } ~
    put {
      path("account" / IntNumber) {
        id => {
          requestInstance {
            request => {
              complete {
                _updateTrader(request.entity.data.asString)
              }
            }
          }
        }
      }~
      path("account" / "connect" / IntNumber) {
        id => {
          complete {
            _reconnect(id)
          }
        }
      }
    } ~
    delete {
      path("account" / IntNumber) {
        id => {
          complete {
             _deleteTrader(id)
          }
        }
      }
    }
  }

  val traderManager = actorRefFactory.actorSelection("/user/" + TradeRouteActor.path)
  private def _getAllTraders(): String = {
    implicit val timeout = Timeout(10 seconds)
    val future = traderManager ? ListTraders
    val result = Await.result(future, timeout.duration).asInstanceOf[Option[Array[Trader]]]
    val retTraders = RetTraderList(0, "success", result)
    implicit val formats: Formats = DefaultFormats
    val json = Extraction.decompose(retTraders)
    compact(render(json))
  }

  private def _reconnect(id: Int): String = {

    """{"code": 1}"""
  }

  private def _createTrader(json: String): String = {
    // 将JSON转换为strategy，加入到strategy
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val trader = jv.extract[Trader]

      traderManager ! NewTrader(trader)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _updateTrader(json: String): String = {
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val trader = jv.extract[Trader]

      traderManager ! UpdateTrader(trader)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }

  }

  private def _deleteTrader(id: Int): String = {
    try {
      //      strategiesManager.removeStrategy(id)
      traderManager ! DeleteTrader(id)
      """{"code":0, "message":"成功删除"}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }
}
