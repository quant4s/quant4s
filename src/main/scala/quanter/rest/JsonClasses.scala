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
case class Strategy(id: Int, var name: String, var runMode: Int, var status: Int, var lang: Option[String], var portfolio: Option[Portfolio])


/**
  *
  * @param cash 现金
  * @param date 日期
  * @param holdings 持有股份
  */
case class Portfolio(cash: Double, date: Date, holdings: Option[List[SecurityHolding]]) {
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
  * @param symbol
  * @param quantity
  * @param price
  * @param orderType 0:限价单 1:市价单 2:...
  * @param orderStatus 0: New
  * @param tradeAccountId 交易接口编号
  */
case class Order(orderNo:Int, symbol: String, quantity: Int, price: Option[Double], orderType: Int, orderStatus: Int, side: Int,
                 openClose: String, tradeAccountId: Int, entrustNo: Option[Int], transNo: Option[Int]) {
  var strategyId: Int = 0
  var securityExchange = "XSHE"
}

case class CancelOrder(orderNo: Int, cancelOrderNo: Int, tradeAccountId: Int) {
  var strategyId: Int = 0
}

case class Transaction(strategyId: Int, orders: Option[List[Order]], cancelOrder: Option[CancelOrder])

case class Trader(id: Option[Int], name: String, brokerType: String, brokerName: String, brokerCode: String, brokerAccount: String, brokerPassword: Option[String], brokerUri: String, brokerServicePwd: Option[String], status: Int = 0)
case class ChannelType(name: String, title: String, desc: String, driver: String)

case class FinanceIndi(name: String, op: String, value: Double, order: Option[String], count: Option[Int] )

case class SecurityPicker(financeIndi: List[FinanceIndi])

// 返回的JSON CLASS
case class RetCode(code: Int, message: String)
case class RetStrategy(code: Int, message: String, strategy: Option[Strategy])
case class RetStrategyList(code: Int, message: String, strategies: Option[Array[Strategy]])
case class RetTrader(code: Int, message: String, trader: Option[Trader])
case class RetTraderList(code: Int, message: String, traders: Option[Array[Trader]])
case class RetChannelTypes(code: Int, message: String, channelTypes: Array[ChannelType])