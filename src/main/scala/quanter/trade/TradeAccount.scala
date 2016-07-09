package quanter.trade

import quanter.orders.Order

/**
  *
  */
trait TradeAccount {

  def onLogin()

  var id : String
  var name: String

  protected def buy(symbol: String, quantity: Int, price: Double)
  protected def sell(symbol: String, quantity: Int, price: Double)

  def keep()
  def executionReport()
  def orderRejected()

  def order(order: Order): Unit = {
    if(order.quantity > 0) {
      buy(order.symbol, order.quantity, order.price)
    }
    else {
      sell(order.symbol, math.abs(order.quantity), order.price)
    }
  }
}
