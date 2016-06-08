package quanter.trade

import quanter.orders.Order

/**
  *
  */
trait TTrade {
  var id : String

  protected def add(symbol: String, quantity: Int, price: Double)

  protected def sell(symbol: String, quantity: Int, price: Double)

  def keep()
  def executionReport()

  def order(order: Order): Unit = {
    if(order.quantity > 0) {
      add(order.symbol, order.quantity, order.price)
    }
    else {
      sell(order.symbol, math.abs(order.quantity), order.price)
    }
  }
}
