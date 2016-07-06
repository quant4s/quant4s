package quanter.actors.trade

import akka.actor.{Actor, ActorRef, Props}
import quanter.rest.{Trader, Transaction}
import quanter.trade.TraderCache

import scala.collection.mutable

case class ListTraders()
case class CreateTrader(trader: Trader)
case class UpdateTrader(trader: Trader)
case class DeleteTrader(id: Int)
case class GetTrader(id: Int)


/**
  * 1、管理交易接口
  * 2、处理订单
  */
class TradeRouteActor extends Actor {
  var traders = new mutable.HashMap[Int, ActorRef]()
  val cache = new TraderCache()

  override def receive: Receive = {
    case ListTraders => _getAllTraders()
    case t: CreateTrader => _createTrader(t.trader)
    case t: UpdateTrader => _updateTrader(t.trader)
    case t: DeleteTrader => _deleteTrader(t.id)
    case t: GetTrader => _getTrader(t.id)


    case tran: Transaction => _handleOrder(tran)
  }

  /**
    * 初始化指定的交易接口
 *
    * @param id
    */
  private def _init(id: Int): Unit = {
    val trader = null
    val ref = context.actorOf(TradeActor.props(trader))

    traders += (id -> ref)
  }

  // CRUD 的操作
  private def _getAllTraders(): Unit = {
    sender ! Some(cache.getAllTraders())
  }

  private def _createTrader(trader: Trader): Unit = {
    sender ! cache.addTrader(trader)
  }

  private def _updateTrader(trader: Trader): Unit = {
    cache.modifyTrader(trader)
  }

  private def _deleteTrader(id: Int): Unit = {
   cache.removeTrader(id)
  }

  private def _getTrader(id: Int): Unit = {
    sender ! cache.getTrader(id)
  }

  /**
    * 将订单发送给合适的交易通道
 *
    * @param tran
    */
  private def _handleOrder(tran: Transaction): Unit = {
    for(order <- tran.orders) {
      // TODO: 将订单保存到数据库

      traders.get(order.tradeId).get ! order
    }
  }
}

object TradeRouteActor {
  def props: Props = {
    Props(classOf[TradeRouteActor])
  }

  val path = "tradeManager"
}

