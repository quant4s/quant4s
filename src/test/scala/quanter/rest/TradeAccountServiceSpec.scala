/**
  *
  */
package quanter.rest

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import quanter.actors.persistence.PersistenceActor
import quanter.actors.strategies.StrategiesManagerActor
import quanter.actors.trade.TradeRouteActor
import spray.http.{HttpEntity, MediaTypes}

/**
  *
  */
class TradeAccountServiceSpec  extends RoutingSpec with TradeAccountService{
  implicit def actorRefFactory = system

  "交易节点 管理, ID: 1001，1002， 1003" should {
    "  创建三个交易节点1001，1002， 1003" in {
      Post("/account", HttpEntity(MediaTypes.`application/json`,
        """{"id": 1001,"name": "SHSE","brokerType":"CTP", "brokerName":"THS", "brokerCode":"2011","brokerAccount":"66666660077","brokerPassword": "password", "brokerUri":"tcp://33.44.55.32:8099","status": 0}"""
      )) ~> tradeAccountServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Post("/account", HttpEntity(MediaTypes.`application/json`,
        """{"id": 1002,"name": "SHSE","brokerType":"CTP", "brokerName":"THS", "brokerCode":"2011","brokerAccount":"66666660077","brokerPassword": "password", "brokerUri":"tcp://33.44.55.32:8099","status": 0}"""
      )) ~> tradeAccountServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Post("/account", HttpEntity(MediaTypes.`application/json`,
        """{"id": 1003,"name": "SHSE","brokerType":"CTP", "brokerName":"THS", "brokerCode":"2011","brokerAccount":"66666660077","brokerPassword": "password", "brokerUri":"tcp://33.44.55.32:8099","status": 0}"""
      )) ~> tradeAccountServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Get("/account/list") ~> tradeAccountServiceRoute ~> check {
        val json = responseAs[String]
        println( json)
        implicit val formats = DefaultFormats
        val jv = parse(json)
        val ret = jv.extract[RetTraderList]
        ret.code === 0
        ret.traders.getOrElse(new Array[Trader](1)).length === 5
      }

      Delete("/account/1001")~> tradeAccountServiceRoute ~> check {
        responseAs[String] === """{"code":0, "message":"成功删除"}"""
      }

      Get("/account/list") ~> tradeAccountServiceRoute ~> check {
        val json = responseAs[String]
        println( json)
        implicit val formats = DefaultFormats
        val jv = parse(json)
        val ret = jv.extract[RetTraderList]
        ret.code === 0
        ret.traders.getOrElse(new Array[Trader](1)).length === 5
      }

    }

    "connect 交易节点" in {
        1===1
    }
  }

}
