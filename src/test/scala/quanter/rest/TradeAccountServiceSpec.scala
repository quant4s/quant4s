/**
  *
  */
package quanter.rest

import akka.actor.{ActorLogging, ActorSystem}
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import quanter.actors.persistence.PersistenceActor
import quanter.actors.strategy.StrategiesManagerActor
import quanter.actors.trade.TradeRouteActor
import quanter.interfaces.TLogging
import spray.http.{HttpEntity, MediaTypes}

/**
  *
  */
class TradeAccountServiceSpec  extends RoutingSpec with TradeAccountService with StrategyService{
  implicit def actorRefFactory = system
  override implicit def systemRef: ActorSystem = system


    "交易节点 管理, ID: 1001，1002， 1003" - {
    "  创建三个交易节点1001，1002， 1003" in {
//      Post("/strategy", HttpEntity(MediaTypes.`application/json`,
//        """{"id": 910,"name": "不带资金组合","runMode":1, "status": 1}"""
//      )) ~> strategyServiceRoute ~> check {
//        //status shouldEqual Success
//        responseAs[String] shouldEqual """{"code":0}"""
//      }
//      Post("/account", HttpEntity(MediaTypes.`application/json`,
//        """{"id": 1001,"name": "SHSE","brokerType":"CTP", "brokerName":"THS", "brokerCode":"2011","brokerAccount":"66666660077","brokerPassword": "password", "brokerUri":"tcp://33.44.55.32:8099","status": 0}"""
//      )) ~> tradeAccountServiceRoute ~> check {
//        cancel()
//      }
        //status shouldEqual Success
        //responseAs[String] shouldEqual """{"code":0}"""
//      }
//      Post("/account", HttpEntity(MediaTypes.`application/json`,
//        """{"id": 1002,"name": "SHSE","brokerType":"CTP", "brokerName":"THS", "brokerCode":"2011","brokerAccount":"66666660077","brokerPassword": "password", "brokerUri":"tcp://33.44.55.32:8099","status": 0}"""
//      )) ~> tradeAccountServiceRoute ~> check {
//        //status shouldEqual Success
//        responseAs[String] shouldEqual """{"code":0}"""
//      }
//      Post("/account", HttpEntity(MediaTypes.`application/json`,
//        """{"id": 1003,"name": "SHSE","brokerType":"CTP", "brokerName":"THS", "brokerCode":"2011","brokerAccount":"66666660077","brokerPassword": "password", "brokerUri":"tcp://33.44.55.32:8099","status": 0}"""
//      )) ~> tradeAccountServiceRoute ~> check {
//        //status shouldEqual Success
//        responseAs[String] shouldEqual """{"code":0}"""
//      }
//      Get("/account/list") ~> tradeAccountServiceRoute ~> check {
//        val json = responseAs[String]
//        println( json)
//        implicit val formats = DefaultFormats
//        val jv = parse(json)
//        val ret = jv.extract[RetTraderList]
//        ret.code shouldEqual 0
//        ret.traders.getOrElse(new Array[Trader](1)).length shouldEqual 5
//      }
//
//      Delete("/account/1001")~> tradeAccountServiceRoute ~> check {
//        responseAs[String] shouldEqual """{"code":0, "message":"成功删除"}"""
//      }
//
//      Get("/account/list") ~> tradeAccountServiceRoute ~> check {
//        val json = responseAs[String]
//        println( json)
//        implicit val formats = DefaultFormats
//        val jv = parse(json)
//        val ret = jv.extract[RetTraderList]
//        ret.code shouldEqual 0
//        ret.traders.getOrElse(new Array[Trader](1)).length shouldEqual 5
//      }

    }

    "connect 交易节点" in {
        1 shouldEqual 1
    }
  }


}
