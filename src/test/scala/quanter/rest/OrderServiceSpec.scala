/**
  *
  */
package quanter.rest

import spray.http.{HttpEntity, MediaTypes}

/**
  *
  */
class OrderServiceSpec  extends RoutingSpec with StrategyService{
  implicit def actorRefFactory = system

  "订单提交," - {
    " 提交数据订阅请求" in {
      Post("/strategy/1/order", HttpEntity(MediaTypes.`application/json`,
        """{"strategyId": 1,"orders":[{"orderNo":1,"quantity":1000,"symbol":"000001.XSHE","orderType":0,"openClose": "O","orderStatus":0,"side": 1, "tradeAccountId":999}]}"""
      )) ~> strategyServiceRoute ~> check {
        responseAs[String] shouldEqual """{"code":0}"""
      }
      Post("/strategy/1/order", HttpEntity(MediaTypes.`application/json`,
        """{"strategyId": 1,"orders":[{"orderNo":2,"quantity":1000,"symbol":"000001.XSHE","orderType":0,"openClose": "O","orderStatus":0,"side": 1, "tradeAccountId":999}]}"""
      )) ~> strategyServiceRoute ~> check {
        responseAs[String] shouldEqual """{"code":0}"""
      }
      Delete("/strategy/1/order/1") ~> strategyServiceRoute ~> check {
        responseAs[String] shouldEqual """{"code":0}"""
      }

    }

  }

}
