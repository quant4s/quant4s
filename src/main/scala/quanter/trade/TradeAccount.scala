package quanter.trade

import quanter.orders.Order

/**
  *
  */
trait TradeAccount {

  def connect(account: String, password: String, servicePwd: Option[String] = None)

  var id : Int
  var name: String

  def buy(symbol: String, quantity: Int, price: Double)
  def sell(symbol: String, quantity: Int, price: Double)
  def buy(symbol: String, quantity: Int)
  def sell(symbol: String, quantity: Int)

  def keep()
  def executionReport()

}
