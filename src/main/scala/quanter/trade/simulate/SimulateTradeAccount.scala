/**
  *
  */
package quanter.trade.simulate

import quanter.trade.TradeAccount

/**
  *
  */
class SimulateTradeAccount extends TradeAccount{
  override def onLogin(): Unit = ???

  override def orderRejected(): Unit = ???

  override protected def buy(symbol: String, quantity: Int, price: Double): Unit = ???

  override def executionReport(): Unit = ???

  override protected def sell(symbol: String, quantity: Int, price: Double): Unit = ???

  override def keep(): Unit = ???

  override var name: String = _
  override var id: String = _
}
