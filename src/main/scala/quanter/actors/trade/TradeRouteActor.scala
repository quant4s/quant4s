package quanter.actors.trade

import akka.actor.{Actor, ActorRef, Props}
import quanter.actors.persistence.PersistenceActor
import quanter.persistence.EOrder
import quanter.rest.{Trader, Transaction}
import quanter.trade.TradeAccountCache
import quanter.trade.simulate.SimulateTradeAccount

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
  val cache = new TradeAccountCache()
  val persisRef = context.actorSelection("/user/" + PersistenceActor.path)

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
    */
  private def _init(): Unit = {
    for(ta <- cache.traders.values) {
      ta.brokerType match {
        case "CTP" =>
        case "LTS" =>
        case "FIX" =>
        case "T2" =>
        case "THS" =>
        case "SIM" =>
      }
    }
    // 从数据库中读取所有的内容
    val trader = new SimulateTradeAccount()
    val ref = context.actorOf(TradeActor.props(trader))

    traders += (trader.id -> ref)
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
    for(order <- tran.orders.get) {
      // TODO: 将订单保存到数据库
      val o = EOrder(None, order.orderNo, order.strategyId, order.symbol, order.orderType, order.side,
        "201606060000", order.quantity, order.openClose, order.price.get, "RMB", order.securityExchange)


      // 发送到相应的交易接口
      traders.get(order.tradeAccountId).get ! order
    }

    if(tran.cancelOrder != None) {
      // TODO: 将取消订单保存到数据库

      // TODO: 找到 订单对应的交易接口
      val accountId = 0
      val order = tran.cancelOrder.get
      traders.get(accountId).get ! order
    }
  }
}

object TradeRouteActor {
  def props: Props = {
    Props(classOf[TradeRouteActor])
  }

  val path = "tradeManager"
}

