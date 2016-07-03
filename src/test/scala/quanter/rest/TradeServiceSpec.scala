/**
  *
  */
package quanter.rest

import quanter.actors.persistence.PersistenceActor
import quanter.actors.strategies.StrategiesManagerActor
import quanter.actors.trade.TradeRouteActor
import spray.http.{HttpEntity, MediaTypes}

/**
  *
  */
class TradeServiceSpec  extends RoutingSpec with TradeService{
  def actorRefFactory = system
  system.actorOf(StrategiesManagerActor.props, StrategiesManagerActor.path)
  system.actorOf(PersistenceActor.props, PersistenceActor.path)
  system.actorOf(TradeRouteActor.props, TradeRouteActor.path)

  "交易节点 管理, ID: 1001，1002， 1003" should {
    "  创建三个策略1001，1002， 1003" in {
      Post("/trade", HttpEntity(MediaTypes.`application/json`,
        """{"id": 910,"name": "不带资金组合","runMode":1, "status": 1"""
      )) ~> tradeServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 911,"name": "带资金组合","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}"""
      )) ~> tradeServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 912,"name": "实盘测试","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}"""
      )) ~> tradeServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Get("/trade/list") ~> tradeServiceRoute ~> check {
        responseAs[String] === """{"code":1}"""
      }
    }
  }

}
