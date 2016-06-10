package quanter.actors.trade

import akka.actor.{Actor, ActorRef, Props}
import quanter.orders.Order

import scala.collection.mutable

/**
  * 1、初始化交易接口
  * 2、处理订单
  */
class TradeRouteActor extends Actor {
  var traders = new mutable.HashMap[String, ActorRef]()
  override def receive: Receive = {
    case order: Order => _handleOrder(order)
  }

  /**
    * 初始化指定的交易接口
 *
    * @param id
    */
  private def _init(id: String): Unit = {
    val trader = null
    val ref = context.actorOf(TradeActor.props(trader))

    traders += (id -> ref)
  }

  /**
    * 将订单发送给合适的交易通道
 *
    * @param order
    */
  private def _handleOrder(order: Order): Unit = {
    traders.get(order.tradeId).get ! order
  }
}

object TradeRouteActor {
  def props(): Props = {
    Props(classOf[TradeRouteActor])
  }

  val path = "tradeManager"
}
