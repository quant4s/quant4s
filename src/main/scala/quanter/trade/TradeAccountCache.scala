/**
  *
  */
package quanter.trade

import quanter.rest.Trader

import scala.collection.mutable

/**
  *
  */
class TradeAccountCache {
  var traders = new mutable.HashMap[Int, Trader]()

  def addTrader(trader: Trader): Unit = {
    traders += (trader.id.getOrElse(0) -> trader)
  }

  def getAllTraders(): Array[Trader] = {
    traders.values.toArray
  }

  def modifyTrader(strategy: Trader): Unit = {
    traders(strategy.id.get) = strategy
  }

  def removeTrader(id: Int): Unit = {
    traders -= id
  }

  def getTrader(id: Int): Option[Trader] = {
    traders.get(id)
  }
}
