package quanter.actors.trade

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import quanter.actors._
import quanter.actors.persistence.PersistenceActor
import quanter.persistence.EOrder
import quanter.rest.{Trader, Transaction}
import quanter.trade.TradeAccountCache
import quanter.trade.simulate.SimulateTradeAccount


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
    case t: NewTrader => _createTrader(t.trader)
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
    implicit val timeout = Timeout(5 seconds)
    val future = persisRef ? ListTraders
    val result = Await.result(future, timeout.duration).asInstanceOf[Option[Array[Trader]]]

    sender ! Some(result)
  }

  private def _createTrader(trader: Trader): Unit = {
    persisRef ! NewTrader(trader)
    // sender ! cache.addTrader(trader)
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

