/**
  *
  */
package quanter.risk

import quanter.rest.{Order, Portfolio}

/**
  *
  */
trait BaseRisk {

  def addRule(rule: String): Unit
  /**
    * 当匹配成功的时候返回true
    * @param portfolio
    * @param order
    * @return
    */
  def matchRule(portfolio: Portfolio, order: Order) : Boolean
}
