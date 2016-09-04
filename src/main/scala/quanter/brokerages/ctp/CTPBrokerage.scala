/**
  *
  */
package quanter.brokerages.ctp

import quanter.brokerages.Brokerage

/**
  *
  */
class CTPBrokerage(pname: String) extends Brokerage("CTP交易接口" + pname) {
  override def isConnected: Boolean = {
    true
  }

  override def disconnect: Unit = {
  }

  override def connect: Unit = {
  }

  override def keep: Unit = {

  }

  override def buy(code: String, price: Double, quantity: Int): Unit = {}

  override def sell(code: String, price: Double, quantity: Int): Unit = {}
}
