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

//  traders += (1 -> Trader(1, "同花顺", "THS","华泰证券","2205","666600166666", "password", "c:\\htzq\\xiadan.exe", Some("servicepwd"), 0),
//    2 -> Trader(2, "华宝LTS模拟", "LTS","华宝证券","2011","020090001340", "password", "tcp://211.144.195.163:44505", Some("servicepwd"), 0),
//    999 -> Trader(999, "仿真账户", "SIM","仿真账户","","", "", "", None, 0),
//    3 -> Trader(3, "上海中期CTP模拟", "CTP","上海中期","2206","65241340", "password", "tcp://216.134.34.456:44505", Some("servicepwd"), 0))

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
