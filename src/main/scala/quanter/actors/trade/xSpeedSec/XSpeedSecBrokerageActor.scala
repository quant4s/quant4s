/**
  *
  */
package quanter.actors.trade.xSpeedSec

import quanter.actors.trade.BrokerageActor
import quanter.rest.{CancelOrder, Order}

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
