/**
  *
  */
package quanter.rest

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http.{ContentType, _}
import StatusCodes._
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import spray.http.HttpCharsets._
import spray.http.MediaTypes._
import spray.httpx.RequestBuilding._
import spray.util.Utils

/**
  *
  */
class Strategy900ServiceSpec extends Specification{
  val system = ActorSystem(Utils.actorSystemNameFrom(getClass))
  val server = system.actorOf(HttpServer.props())
  val probe  = TestProbe()(system)

  "执行一个策略, ID: 900，下单的过程" should {
    "创建一个策略" in {
//      val probe  = TestProbe()(system)
      probe.send(server, Post("/strategy", HttpEntity(MediaTypes.`application/json`,
        """{"id": 900,"name": "实盘测试","cash": 100000, ""}"""
      )))
      probe.expectMsg(HttpResponse(entity = HttpEntity(ContentType(`text/plain`, `UTF-8`), """{"code":0}""")))
  success
    }

    "启动策略" in {
      probe.send(server, Post("/strategy/run/900"))
      probe.expectMsg(HttpResponse(entity = HttpEntity(ContentType(`text/plain`, `UTF-8`), """{"code":0}""")))
      success
    }

    "购买一只股票" in {
      failure
    }
  }
}
