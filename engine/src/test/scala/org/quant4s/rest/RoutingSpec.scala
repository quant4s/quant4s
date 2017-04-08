/**
  *
  */
package org.quant4s.rest

import org.quant4s.actors.persistence.PersistenceActor
import org.quant4s.actors.strategy.StrategiesManagerActor
import org.quant4s.actors.trade.TradeRouteActor
import org.scalatest.{FreeSpec, Matchers}
import spray.routing.Directives
import spray.testkit.ScalatestRouteTest

abstract class RoutingSpec extends FreeSpec with Matchers with Directives with ScalatestRouteTest {

  system.actorOf(StrategiesManagerActor.props, StrategiesManagerActor.path)
  system.actorOf(PersistenceActor.props, PersistenceActor.path)
  system.actorOf(TradeRouteActor.props, TradeRouteActor.path)



//  val Ok = HttpResponse()
//  val completeOk = complete(Ok)
//  def echoComplete[T]: T ⇒ Route = { x ⇒ complete(x.toString) }
//  def echoComplete2[T, U]: (T, U) ⇒ Route = { (x, y) ⇒ complete(s"$x $y") }
}
