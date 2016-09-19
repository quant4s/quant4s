/**
  *
  */
package quanter.risk

import quanter.actors.strategy.StrategyActor.StrategyContext
import quanter.rest.{Order, Portfolio}

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
