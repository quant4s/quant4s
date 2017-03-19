/**
  *
  */
package org.quant4s.risk

import org.quant4s.actors.strategy.StrategyActor.StrategyContext
import org.quant4s.rest.Order

/**
  *
  */
trait BaseRisk {

  def addRule(rule: String): Unit
  /**
    * 当匹配成功的时候返回true
    * @param context
    * @param order
    * @return
    */
  def matchRule(context: StrategyContext, order: Order) : Boolean
}
