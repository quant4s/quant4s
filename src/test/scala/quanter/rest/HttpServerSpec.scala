/**
  *
  */
package quanter.rest

import org.specs2.mutable.Specification
import akka.testkit.TestProbe
import akka.actor._
import spray.httpx.RequestBuilding._
import spray.util._
import spray.http._
import MediaTypes._
import HttpCharsets._

/**
  *
  */
class HttpServerSpec extends Specification {
  val system = ActorSystem(Utils.actorSystemNameFrom(getClass))
  val server = system.actorOf(HttpServer.props())

  "'strategyRoute' 指令" should {
    "POST /strategy 创建一个策略" in {
      val probe  = TestProbe()(system)
      probe.send(server, Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 1,"name": "实盘测试","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}"""
      )))
      probe.expectMsg(HttpResponse(entity = HttpEntity(ContentType(`text/plain`, `UTF-8`), """{"code":0}""")))
      success
    }

    "GET /strategy/list 将获取策略列表" in {
      val probe = TestProbe()(system)
      probe.send(server, Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 2,"name": "实盘测试","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}"""
      )))
      probe.send(server, Get("/strategy/list"))
      probe.expectMsg(HttpResponse(entity = HttpEntity(ContentType(`text/plain`, `UTF-8`), """[{"id":1,"name":"unnamed"},{"id":2,"name":"unnamed"}]""")))
      success
    }

    "GET /strategy/[id] 将获取一个策略的详细(资金，仓位)信息" in {
      val probe = TestProbe()(system)
      probe.send(server, Get("/strategy/1"))
      probe.expectMsg(HttpResponse(entity = HttpEntity(ContentType(`text/plain`, `UTF-8`), """{"id":1,"name":"unnamed","cash":1000}""")))
      success
    }

    "DELETE /strategy/[id] 将删除一个策略" in {
      val probe = TestProbe()(system)
      probe.send(server, Delete("/strategy/1"))
      probe.expectMsg(HttpResponse(entity = HttpEntity(ContentType(`text/plain`, `UTF-8`), """{"code":0, "message":"成功删除"}""")))
      success
    }

    "GET /strategy/backtest/[id] 将获取一个策略的回测报告" in {
      val probe = TestProbe()(system)
      probe.send(server, Get("/strategy/backtest/1"))

      success
    }

    "GET /strategy/backtest/history/[id] 将获取一个策略的回测交易记录报告" in {
      val probe = TestProbe()(system)
      probe.send(server, Get("/strategy/backtest/history/1"))
      success
    }

    "GET /strategy/real/history/[id] 获取实盘历史记录" in {
      val probe = TestProbe()(system)
      probe.send(server, Get("/strategy/real/history/1"))
      success

    }

  }

  "'orderRoute' 指令" should {

    "POST /order 用于创建一个订单" in {
      val probe = TestProbe()(system)
      probe.send(server, Post("/order", HttpEntity(MediaTypes.`application/json`,"""{"id":123}""")))
      probe.expectMsg(HttpResponse(entity = HttpEntity(ContentType(`text/plain`, `UTF-8`), "成功创建订单")))
      success
    }
//
//    "/order PUT 用于更新一个订单" in {
//      val probe = TestProbe()(system)
//      probe.send(server, Put("/order"))
//      probe.expectMsg(HttpResponse(entity = HttpEntity(ContentType(`text/plain`, `UTF-8`), """成功更新订单""")))
//      success
//    }
//
//    "/order DELETE 用于删除一个订单" in {
//      val probe = TestProbe()(system)
//      probe.send(server, Delete("/order"))
//      success
//    }
  }

  step(system.shutdown())
}
