/**
  *
  */
package quanter.trade

import quanter.rest.Trader

import scala.collection.mutable

/**
  *
  */
class TraderManager {
  var traders = new mutable.HashMap[Int, Trader]()

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
