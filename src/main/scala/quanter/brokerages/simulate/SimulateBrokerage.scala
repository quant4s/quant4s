/**
  *
  */
package quanter.brokerages.simulate

import quanter.brokerages.Brokerage


/**
  *
  */
class SimulateBrokerage(pname: String = "") extends Brokerage("模拟交易接口" + pname) {

  override def disconnect: Unit = {
    // NOP
  }

  override def isConnected: Boolean = true

  override def connect: Unit = {
    // NOP
  }

  override def keep: Unit ={
    // NOP
  }

}
