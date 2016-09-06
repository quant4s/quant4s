package quanter.actors.trade

import scala.collection.mutable
import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import quanter.actors._
import quanter.actors.persistence.PersistenceActor
import quanter.actors.provider.QuerySnapData
import quanter.brokerages.ctp.CTPBrokerage
import quanter.interfaces.TBrokerage
import quanter.persistence.EOrder
import quanter.rest.{Trader, Transaction}
import quanter.trade.TradeAccountCache
import quanter.trade.simulate.SimulateBrokerage

import scala.concurrent.Await


/**
  * 1、管理交易接口
  * 2、处理订单
  */
case class InitTradeRoute()
class TradeRouteActor extends Actor with ActorLogging{
  var traderAccounts = new mutable.HashMap[Int, ActorRef]()
  val cache = new TradeAccountCache()
  val persisRef = context.actorSelection("/user/" + PersistenceActor.path)


  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    //_init()
//    context.system.scheduler.schedule(0 seconds, 3 seconds, self, new InitTradeRoute())
  }

  override def receive: Receive = {
    case t: InitTradeRoute => _init()
    case t: ListTraders => _getAllTraders()
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
    implicit val timeout = Timeout(5 seconds)
    val future = persisRef ? new ListTraders()
    val result = Await.result(future, timeout.duration).asInstanceOf[Array[Trader]]

    // 从数据库中读取所有的内容
    // FIXME: 从配置文件中读取映射关系
    for(t <- result) {
       t.brokerType match {
        case "THS" => log.info("启动同花顺交易接口")
        case "CTP" => log.info("启动CTP交易接口")
          val brokerage = new CTPBrokerage(t.name)
          val ref = context.actorOf(BrokerageActor.props(brokerage))
          traderAccounts += (t.id.get -> ref)

        case "SIM" => {
           val brokerage = new SimulateBrokerage(t.name)
           val ref = context.actorOf(BrokerageActor.props(brokerage))
          traderAccounts += (t.id.get -> ref)
        }
        case _ => log.info("启动监控")
      }

    }


  }

  // CRUD 的操作
  private def _getAllTraders(): Unit = {
    implicit val timeout = Timeout(5 seconds)
    val future = persisRef ? new ListTraders()
    val result = Await.result(future, timeout.duration).asInstanceOf[Array[Trader]]
    sender ! Some(result)
  }

  private def _createTrader(trader: Trader): Unit = {
    persisRef ! NewTrader(trader)
  }

  private def _updateTrader(trader: Trader): Unit = {
    //cache.modifyTrader(trader)
    persisRef ! new UpdateTrader(trader)
  }

  private def _deleteTrader(id: Int): Unit = {
   //cache.removeTrader(id)
    persisRef ! new DeleteTrader(id)
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
    if(tran.orders != None) {
      for (order <- tran.orders.get) {
       order.strategyId = tran.strategyId
        // 发送到相应的交易接口
        persisRef ! new NewOrder(order)
        traderAccounts.get(order.tradeAccountId).get ! order
        log.info("接收到策略%d订单%d, 交易接口为%d".format(order.strategyId, order.orderNo, order.tradeAccountId))
      }
    }

    if(tran.cancelOrder != None) {
      val accountId = 0
      val order = tran.cancelOrder.get
      order.strategyId = tran.strategyId
      // 将取消订单保存到数据库，并发送到交易接口
      persisRef ! new RemoveOrder(order)
      traderAccounts.get(accountId).get ! order
      log.info("取消策略%d订单%d,交易接口为%d".format(order.strategyId, order.cancelOrderNo, order.tradeAccountId))
    }
  }
}

object TradeRouteActor {
  def props: Props = {
    Props(classOf[TradeRouteActor])
  }

  val path = "traderRouter"
}

