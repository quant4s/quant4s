/**
  *
  */
package quanter.rest

import org.specs2.mutable.Specification
import quanter.actors.persistence.PersistenceActor
import quanter.actors.strategies.StrategiesManagerActor
import quanter.actors.trade.TradeRouteActor
import spray.testkit.Specs2RouteTest
import spray.http.HttpResponse
import spray.routing.{Directives, Route}

abstract class RoutingSpec extends Specification with Directives with Specs2RouteTest {

  system.actorOf(StrategiesManagerActor.props, StrategiesManagerActor.path)
  system.actorOf(PersistenceActor.props, PersistenceActor.path)
  system.actorOf(TradeRouteActor.props, TradeRouteActor.path)



  val Ok = HttpResponse()
  val completeOk = complete(Ok)
  def echoComplete[T]: T ⇒ Route = { x ⇒ complete(x.toString) }
  def echoComplete2[T, U]: (T, U) ⇒ Route = { (x, y) ⇒ complete(s"$x $y") }
}
