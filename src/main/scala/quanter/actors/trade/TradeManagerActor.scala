package quanter.actors.trade

import akka.actor.{Actor, ActorRef}
import quanter.orders.Order
import quanter.trade.HuaTai.HuaTaiWebTrader

import scala.collection.mutable

/**
  * 1、初始化交易接口
  * 2、处理订单
  */
class TradeManagerActor extends Actor {
  var traders = new mutable.HashMap[String, ActorRef]()
  override def receive: Receive = ???

  /**
    * 初始化指定的交易接口
    * @param id
    */
  private def _init(id: String): Unit = {
    val trader = new HuaTaiWebTrader()
    val ref = context.actorOf(TradeActor.props(trader))

    traders += (id -> ref)
  }

  /**
    * 将订单发送给合适的交易通道
    * @param order
    */
  private def _handleOrder(order: Order): Unit = {
    traders.get(order.tradeId).get ! order
  }

}
