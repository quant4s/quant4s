package quanter

import org.scalatest.{FunSpec, Matchers}

/**
  * Created by joe on 16-3-11.
  */
abstract class QuanterUnitSpec extends FunSpec with Matchers {
  val epsilon = 0.0001
}
