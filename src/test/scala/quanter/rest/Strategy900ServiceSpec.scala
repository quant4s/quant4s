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
import quanter.actors.strategies.StrategiesManagerActor
import spray.http.HttpCharsets._
import spray.http.MediaTypes._
import spray.httpx.RequestBuilding._
import spray.routing.Directives
import spray.util.Utils

/**
  *
  */
class Strategy900ServiceSpec extends Specification{
  val system = ActorSystem(Utils.actorSystemNameFrom(getClass))
  val server = system.actorOf(HttpServer.props())
  system.actorOf(StrategiesManagerActor.props, StrategiesManagerActor.path)

//  val system = ActorSystem(Utils.actorSystemNameFrom(getClass))
//  val server = system.actorOf(HttpServer.props())
//  val probe  = TestProbe()(system)

//  "执行一个策略, ID: 900，下单的过程" should {
//    "创建一个策略" in {
//    }
//
//    "启动策略" in {
//    }
//
//    "查看策略的信息" in {
//
//    }
//
//    "购买一只股票" in {
//    }
//  }
}
