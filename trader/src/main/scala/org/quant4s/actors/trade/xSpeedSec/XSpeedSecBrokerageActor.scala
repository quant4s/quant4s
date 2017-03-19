/**
  *
  */
package org.quant4s.actors.trade.xSpeedSec

import org.quant4s.actors.trade.BrokerageActor
import org.quant4s.rest.{CancelOrder, Order}

/**
  *
  */
class XSpeedSecBrokerageActor extends BrokerageActor{
  override def queryCapital(): Unit = ???

  override def queryOrders(): Unit = ???

  override def order(order: Order): Unit = ???

  override def cancel(order: CancelOrder): Unit = ???

  override def queryPosition(symbol: String): Unit = ???

  override def queryUnfinishOrders(): Unit = ???
}
