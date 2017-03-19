/**
  *
  */
package org.quant4s.risk

import org.quant4s.actors.strategy.StrategyActor.StrategyContext
import org.quant4s.rest.Order

import scala.collection.mutable.ArrayBuffer

/**
  * 黑名单规则
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
