/**
  *
  */
package quanter.brokerages.oanda

import quanter.brokerages.Brokerage
import quanter.rest.Trader

/**
  *
  */
class OandaBrokerage(pname: String) extends Brokerage(pname){
  def this() {
    this("")
  }
  override def isConnected: Boolean = ???

  override def disconnect: Unit = ???

  override def buy(code: String, price: Double, quantity: Int): Unit = ???

  override def sell(code: String, price: Double, quantity: Int): Unit = ???

  override def connect: Unit = ???

  override def keep: Unit = ???

  override var accountInfo: Trader = _
}
