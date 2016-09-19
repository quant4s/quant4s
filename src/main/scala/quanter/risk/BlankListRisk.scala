/**
  *
  */
package quanter.risk

import quanter.actors.strategy.StrategyActor.StrategyContext
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

  override def matchRule(context: StrategyContext, order: Order): Boolean = {
    var ret = false
    for(bs <- blankSymbols) {
      if(order.symbol.matches(bs)) ret =true    // FIXME: 优化是
    }

    ret
  }
}
