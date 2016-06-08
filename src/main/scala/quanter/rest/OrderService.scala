package quanter.rest

import akka.actor.{Actor, ActorLogging}
import spray.http.StatusCode
import spray.http.StatusCodes.Success
import spray.routing.HttpService
import org.json4s._
import org.json4s.jackson.JsonMethods._
import quanter.orders.Order

/**
  *
  */
trait OrderService extends HttpService {
  val orderServiceRoute = {
    post {
      path("order") {
        entity(as[String]) { json =>
          complete {
            _createOrder(json)
          }
        }
      }
    }~
    put {
      path("order") {
        complete("更新数据")
      }
    }
  }

  private def _createOrder(json: String): String = {
    val jv = parse(json)
    val v: JValue = (jv \ "orders")(0) \ "symbol"

    // 创建一个订单对象
     val order = Order.createOrder(json)

    // 保存订单对象

    // 下单到交易路由

    "ok"
  }

}
