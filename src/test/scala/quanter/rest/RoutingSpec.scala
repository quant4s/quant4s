/**
  *
  */
package quanter.rest

import org.scalatest.{FreeSpec, Matchers}
import quanter.actors.persistence.PersistenceActor
import quanter.actors.strategies.StrategiesManagerActor
import quanter.actors.trade.TradeRouteActor
import spray.testkit.{ScalatestRouteTest}
import spray.routing.{Directives}

abstract class RoutingSpec extends FreeSpec with Matchers with Directives with ScalatestRouteTest {

  system.actorOf(StrategiesManagerActor.props, StrategiesManagerActor.path)
  system.actorOf(PersistenceActor.props, PersistenceActor.path)
  system.actorOf(TradeRouteActor.props, TradeRouteActor.path)



//  val Ok = HttpResponse()
//  val completeOk = complete(Ok)
//  def echoComplete[T]: T ⇒ Route = { x ⇒ complete(x.toString) }
//  def echoComplete2[T, U]: (T, U) ⇒ Route = { (x, y) ⇒ complete(s"$x $y") }
}
