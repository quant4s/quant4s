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
    put {
      path("order") {
        requestInstance {
          request =>
            complete {
              request.entity.data.asString
            }
        }
      }
    }~
    delete {
      path("order" / IntNumber) {
        id =>
        complete("取消订单")
      }
    }
  }

  private def _createOrder(json: String): String = {
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val orders = jv.extract[Transaction]

      tradeRoute ! orders
"""{"code": 0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
    // val jv = parse(json)
    // val v: JValue = (jv \ "orders")(0) \ "symbol"

    // TODO: 根据json 创建多个订单对象
   //  val order = v.extract[Order]
    // 对每一个订单对象 下单路由
    // val order = Order.createOrder(json)
    //tradeRoute ! order
    ""
  }

}
