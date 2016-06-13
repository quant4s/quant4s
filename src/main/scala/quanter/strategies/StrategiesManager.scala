/**
  *
  */
package quanter.strategies

import quanter.rest.Strategy

import scala.collection.mutable

/**
  * 管理系统中所有的策略
  */
class StrategiesManager {
  var strategies = new mutable.HashMap[Int, Strategy]()

  def addStrategy(strategy: Strategy): Unit = {
    strategies += (strategy.id -> strategy)
  }

  def getAllStrategies(): Array[Strategy] = {
    strategies.values.toArray
  }

  def modifyStrategy(strategy: Strategy): Unit = {
    strategies(strategy.id) = strategy
  }

  def removeStrategy(id: Int): Unit = {
    strategies -= id
  }

  def getStrategy(id: Int): Option[Strategy] = {
    strategies.get(id)
  }
}
