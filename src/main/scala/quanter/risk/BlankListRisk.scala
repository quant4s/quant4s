/**
  *
  */
package quanter.risk

import quanter.rest.{Order, Portfolio}

import scala.collection.mutable.ArrayBuffer

/**
  *
  */
class BlankListRisk extends BaseRisk{
  val blankSymbols = new ArrayBuffer[String]()

  def addRule(rule: String): Unit = {
    for(symbol <- rule.split(",")) {
      blankSymbols += symbol
    }

  }

  override def matchRule(portfolio: Portfolio, order: Order): Boolean = {
    var ret = false
    for(bs <- blankSymbols) {
      if(order.symbol.matches(bs)) ret =true    // FIXME: 优化是
    }

    ret
  }
}
