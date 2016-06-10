package quanter.rest

import akka.actor.{Actor, ActorLogging}
import spray.http.StatusCode
import spray.http.StatusCodes.Success
import spray.routing.HttpService
import org.json4s._
import org.json4s.jackson.JsonMethods._
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
              request.entity.data.asString
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
