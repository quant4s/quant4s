/**
  *
  */
package quanter.rest

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http.HttpResponse
import spray.routing.{Directives, Route}

abstract class RoutingSpec extends Specification with Directives with Specs2RouteTest {

  val Ok = HttpResponse()
  val completeOk = complete(Ok)

  def echoComplete[T]: T ⇒ Route = { x ⇒ complete(x.toString) }
  def echoComplete2[T, U]: (T, U) ⇒ Route = { (x, y) ⇒ complete(s"$x $y") }
}
