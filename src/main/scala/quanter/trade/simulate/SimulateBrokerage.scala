/**
  *
  */
package quanter.trade.simulate

import quanter.brokerages.Brokerage

/**
  *
  */
class SimulateBrokerage(pname: String) extends Brokerage("仿真交易接口" + pname){



//  override def buy(symbol: String, quantity: Int, price: Double): Unit = ???
//  override def buy(symbol: String, quantity: Int): Unit = ???
//  override def executionReport(): Unit = ???
//  override def sell(symbol: String, quantity: Int, price: Double): Unit = ???
//  override def sell(symbol: String, quantity: Int): Unit = ???
//  override def keep(): Unit = ???

  name = "仿真账户"

  override def connect: Unit = {}
  override def disconnect(): Unit = {}

  override def isConnected: Boolean = true
  override def keep(): Unit = {}

  override def buy(code: String, price: Double, quantity: Int): Unit = {}

  override def sell(code: String, price: Double, quantity: Int): Unit = {}
}
