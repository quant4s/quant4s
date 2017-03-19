/**
  *
  */
package org.quant4s.actors.persistence

import java.util.Date

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, Props}
import org.quant4s.actors.NewOrder
import org.quant4s.actors.trade.{OrderCancelResult, OrderDealResult, OrderStatusResult}
import org.quant4s.persistence._
import org.quant4s.rest.{CancelOrder, Order}

/**
  * 委托单及其相关回报持久层
  * 1. 增加一个委托单（默认状态 0:待报 ）
  * 1. 更新委托单的状态（1: 已报 | 2: 部分成交 | 3: 全部成交 | 4: 废单
  * 2. 增加成交回报
  * 3. 取消订单
  * 4. 修改委托单（TBD）
  *
  */
class OrderPersistorActor extends BasePersistorActor{
  import profile.simple._

  val orderDao = new OrderDao
  val transDao = new TransactionDao
  override def receive: Receive = {
    case action: NewOrder => _insertOrder(action.order)
    case r: OrderStatusResult => _updateOrderStatus(r)
    case r: OrderDealResult => _insertTradeTransaction(r)
    case r: OrderCancelResult => _cancelOrder(r)
//    case o: CancelOrder => _cancelOrder(o) // 取消订单的功能参见 OrderStatusResult
  }


  private def _insertOrder(order: Order): Unit = {
    val o = EOrder(None, order.orderNo, order.strategyId, order.tradeAccountId, order.symbol, order.orderType, order.side, 1, new Date(), order.quantity, order.price.getOrElse(0), "RMB", order.securityExchange, 0)
    val o1 = orderDao.insert(o)

    sender ! o1
  }

  private def _cancelOrder(o: OrderCancelResult): Unit = {
    log.debug("[_cancelOrder]取消订单，")
    orderDao.cancel(o)
  }

  private def _updateOrderStatus(o: OrderStatusResult): Unit = {
    log.debug("[OrderPersistorActor._updateOrderStatus]更新委托单状态")
    orderDao.updateStatus(o)
  }

  private def _insertTradeTransaction(o: OrderDealResult): Unit = {
    log.debug("[OrderPersistorActor._insertTradeTransaction]插入成交回报到数据库")
//    val o = ETransaction(None, order.orderNo, order.strategyId, order.tradeAccountId, "000001.shse", 3, 1,1, new Date(), 100, 100, "RMB", "", 0)
    val t = ETradeTransaction(None, o.strategyId, 3, "", "", o.orderNo, o.contractCode, o.dealVol, o.mdDate, o.orderSysID, o.dealPrice)

    transDao.insert(t)
  }

}

object OrderPersistorActor {
  def props = {
    Props(classOf[OrderPersistorActor])
  }

  val path = "order_persistence"
}