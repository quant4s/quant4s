/**
  *
  */
package quanter.trade

import quanter.rest.Trader

import scala.collection.mutable

/**
  *
  */
class TraderCache {
  var traders = new mutable.HashMap[Int, Trader]()

  traders += (1 -> Trader(1, "XSHE", "username", "password", "servicepwd", 0), 2 -> Trader(2, "XSHG", "username", "password", "servicepwd", 0))

  def addTrader(trader: Trader): Unit = {
    traders += (trader.id -> trader)
  }

  def getAllTraders(): Array[Trader] = {
    traders.values.toArray
  }

  def modifyTrader(strategy: Trader): Unit = {
    traders(strategy.id) = strategy
  }

  def removeTrader(id: Int): Unit = {
    traders -= id
  }

  def getTrader(id: Int): Option[Trader] = {
    traders.get(id)
  }
}
