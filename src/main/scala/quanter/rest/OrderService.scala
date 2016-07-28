package quanter.rest

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import spray.routing.HttpService
import quanter.actors.trade.TradeRouteActor

/**
  *
  */
trait OrderService extends HttpService {
  val tradeRoute = actorRefFactory.actorSelection(TradeRouteActor.path)

  val orderServiceRoute = {
    post {
      path("order") {
        requestInstance {
          request =>
            complete {
              _createOrder(request.entity.data.asString)
            }
        }
      }
    }~
    delete {
      path("order" / Rest) {
        param =>
        complete{
          _cancelOrder(param)
        }
      }
    }
  }

  private def _createOrder(json: String): String = {
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val transaction = jv.extract[Transaction]
      for(order <- transaction.orders.get) {
        order.strategyId = transaction.strategyId
      }
      tradeRoute ! transaction
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _cancelOrder(param: String): String = {
    try {
      val ids = param.split("-")
      val trans = Transaction(ids(0).toInt, None, Some(CancelOrder(ids(1).toInt)))
      tradeRoute ! trans
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }

  }

}
