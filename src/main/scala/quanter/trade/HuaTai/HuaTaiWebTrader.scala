package quanter.trade.HuaTai

import quanter.trade.TTrade

/**
  *
  */
class HuaTaiWebTrader extends TTrade {
  override var id: String = _

  override def executionReport(): Unit = ???

  override def keep(): Unit = ???

  override protected def add(symbol: String, quantity: Int, price: Double): Unit = ???

  override protected def sell(symbol: String, quantity: Int, price: Double): Unit = ???
}
