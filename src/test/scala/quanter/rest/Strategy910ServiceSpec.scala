/**
  *
  */
package quanter.rest

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import quanter.actors.persistence.PersistenceActor
import quanter.actors.strategies.StrategiesManagerActor
import spray.http.{HttpEntity, MediaTypes}

/**
  * 策略 CRUD 的操作测试
  */
class Strategy910ServiceSpec extends RoutingSpec with StrategyService{
  implicit def actorRefFactory = system

  "策略管理全过程, ID: 910，911，912" should {
    "  创建三个策略910, 911, 912" in {
      Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 910,"name": "不带资金组合","runMode":1, "status": 1}"""
      )) ~> strategyServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 911,"name": "带资金组合","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}"""
      )) ~> strategyServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 912,"name": "实盘测试","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}"""
      )) ~> strategyServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Get("/strategy/list")~> strategyServiceRoute ~> check {
        val json = responseAs[String]
        println( json)
        implicit val formats = DefaultFormats
        val jv = parse(json)
        val ret = jv.extract[RetStrategyList]
        ret.code === 0
        ret.strategies.getOrElse(new Array[Strategy](1)).length === 3
      }
    }

    "  读取存在的策略910" in {
      Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 910,"name": "实盘测试","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}"""
      )) ~> strategyServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Get("/strategy/910") ~> strategyServiceRoute ~> check {
        val json = responseAs[String]
        implicit val formats = DefaultFormats
        val jv = parse(json)
        val ret = jv.extract[RetStrategy]
        ret.code === 0
        ret.strategy.get.portfolio.get.cash === 100000.0
      }
    }

    "  读取不存在的策略918" in {
      Get("/strategy/918") ~> strategyServiceRoute ~> check {
        responseAs[String] === """{"code":1}"""
      }
    }

    "更新策略912" in {
      Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 912,"name": "实盘测试","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}"""
      )) ~> strategyServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Put("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 912,"name": "实盘测试","runMode":1, "status": 1, "portfolio": {"cash":90000, "date":"2004-09-04T18:06:22Z"}}"""
      )) ~> strategyServiceRoute ~> check {
        responseAs[String] === """{"code":0}"""
      }
      Get("/strategy/912") ~> strategyServiceRoute ~> check {
        val json = responseAs[String]
        implicit val formats = DefaultFormats
        val jv = parse(json)
        val ret = jv.extract[RetStrategy]
        ret.code === 0
        ret.strategy.get.id === 912
        ret.strategy.get.portfolio.get.cash === 90000
      }
    }

    "删除策略913" in {
      Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 913,"name": "实盘测试","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}"""
      )) ~> strategyServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Delete("/strategy/913") ~> strategyServiceRoute ~> check {
        responseAs[String] === """{"code":0, "message":"成功删除"}"""
      }
      Get("/strategy/913") ~> strategyServiceRoute ~> check {
        responseAs[String] === """{"code":1}"""
      }
    }

    "运行一个策略910" in {
      Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 910,"name": "实盘测试","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}"""
      )) ~> strategyServiceRoute ~> check {
        //status === Success
        responseAs[String] === """{"code":0}"""
      }
      Post("/strategy/run/910") ~> strategyServiceRoute ~> check {
        responseAs[String] === """{"code":0}"""
      }
      Get("/strategy/910") ~> strategyServiceRoute ~> check {
        val json = responseAs[String]
        implicit val formats = DefaultFormats
        val jv = parse(json)
        val ret = jv.extract[RetStrategy]
        ret.code === 0
        ret.strategy.get.id === 910
        ret.strategy.get.status === 1
      }
    }
  }
}
