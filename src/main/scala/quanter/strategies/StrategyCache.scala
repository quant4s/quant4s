/**
  *
  */
package quanter.strategies

import quanter.rest.{TradeAccount, Strategy}

import scala.collection.mutable

/**
  * 管理系统中所有的策略
  */
class StrategyCache {
  var strategies = new mutable.HashMap[Int, Strategy]()

//  strategies += (1 -> Strategy(1, "demo 1", 1, 1, Some("C#"), None), 2 -> Strategy(2, "demo 2", 1, 1, Some("C#"), None))

  def addStrategy(strategy: Strategy): Unit = {
    strategies += (strategy.id -> strategy)
  }

  def getAllStrategies(): Array[Strategy] = {
    strategies.values.toArray
  }

  def updateStrategy(strategy: Strategy): Unit = {
    strategies(strategy.id) = strategy
  }

  def removeStrategy(id: Int): Unit = {
    strategies -= id
  }

  def getStrategy(id: Int): Option[Strategy] = {
    strategies.get(id)
  }
}
