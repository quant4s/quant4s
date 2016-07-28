/**
  *
  */
package quanter.trade.simulate

import quanter.trade.TradeAccount

/**
  *
  */
class SimulateTradeAccount extends TradeAccount{
  override def connect(account: String, password: String, servicePwd: Option[String]): Unit = ???

  override def buy(symbol: String, quantity: Int, price: Double): Unit = ???

  override def buy(symbol: String, quantity: Int): Unit = ???

  override def executionReport(): Unit = ???

  override def sell(symbol: String, quantity: Int, price: Double): Unit = ???

  override def sell(symbol: String, quantity: Int): Unit = ???

  override def keep(): Unit = ???

  override var name: String = "仿真账户"
  override var id: Int = 999
}
