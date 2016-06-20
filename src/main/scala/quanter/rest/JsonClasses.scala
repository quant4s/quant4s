/**
  *
  */
package quanter.rest

import java.util.Date

/**
  *
  * @param id
  * @param name
  * @param runMode  1: 实盘  2: 回测 3: 模拟盘
  * @param status   0: 未运行 1: 运行
  * @param lang
  * @param portfolio
  */
case class Strategy(id: Int, var name: String, var runMode: Int, var status: Int, var lang: Option[String], var portfolio: Option[Portfolio]) {
}

/**
  *
  * @param cash 现金
  * @param date 日期
  * @param holdings 持有股份
  */
case class Portfolio(cash: Double, date: Date, holdings: List[SecurityHolding]) {
}

/**
  *
  * @param symbol 证券
  * @param quantity 数量
  * @param totalCost 总成本
  */
case class SecurityHolding(symbol: String, quantity: Long, totalCost: Double) {
  var lastPrice = 10.2

  def averageCost = totalCost / quantity
  def marketValue = quantity * lastPrice
}

/**
  *
  * @param id
  * @param symbol
  * @param quantity
  * @param price
  * @param orderType 0:限价单 1:市价单 2:...
  * @param orderStatus 0: New
  * @param tradeId 交易接口编号
  */
case class Order(id:Int, symbol: String, quantity: Int, price: Option[Double], orderType: Int, orderStatus: Int, tradeId: Int) {
  var strategyId: Int = 0
}

case class Transaction(strategyId: Int, orders: List[Order])

case class Trader(id: Int, name: String, username: String, password: String, servicePwd: String, status: Int)