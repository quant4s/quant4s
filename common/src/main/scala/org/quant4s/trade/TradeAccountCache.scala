/**
  *
  */
package org.quant4s.trade

import org.quant4s.rest.TradeAccount

import scala.collection.mutable

/**
  *
  */
class TradeAccountCache {
  var traders = new mutable.HashMap[Int, TradeAccount]()

  def addTrader(trader: TradeAccount): Unit = {
    traders += (trader.id.getOrElse(0) -> trader)
  }

  def getAllTraders(): Array[TradeAccount] = {
    traders.values.toArray
  }

  def modifyTrader(strategy: TradeAccount): Unit = {
    traders(strategy.id.get) = strategy
  }

  def removeTrader(id: Int): Unit = {
    traders -= id
  }

  def getTrader(id: Int): Option[TradeAccount] = {
    traders.get(id)
  }
}
