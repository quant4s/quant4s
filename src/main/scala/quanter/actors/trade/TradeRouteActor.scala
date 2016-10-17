package quanter.actors.trade

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.util.Timeout
import quanter.actors._
import quanter.actors.persistence.PersistenceActor
import quanter.config.Settings
import quanter.interfaces.TBrokerage
import quanter.rest.Trader

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


/**
  * 1、管理交易接口
  * 2、处理订单
  */
case class InitTradeRoute()
class TradeRouteActor extends Actor with ActorLogging{
  val persisRef = context.actorSelection("/user/" + PersistenceActor.path)
  val providers = _initProviders()


  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    persisRef ! new ListTraders()
    //context.system.scheduler.schedule(0 seconds, 3 seconds, self, new InitTradeRoute())
  }

  override def receive: Receive = {
    case t: ListTraders => _getAllTraders()
    case t: NewTrader => _createTrader(t.trader)
    case t: UpdateTrader => _updateTrader(t.trader)
    case t: DeleteTrader => _deleteTrader(t.id)

    // return
    case t: Array[Trader] => _createTradeAccountActor(t)
    case t: Trader => _traderCreated(t)
  }

  /**
    * 初始化指定的交易接口
    */
  private def _initProviders(): mutable.HashMap[String, String] = {
    val setting = Settings(context.system)
    val map = new mutable.HashMap[String, String]()
    for(i <- 0 until setting.channelTypes.size()) {
      val provider = setting.channelTypes.get(i).asInstanceOf[java.util.HashMap[String, String]]
      map.put(provider.get("name"), provider.get("driver"))
    }
    map
  }

  private def _createTradeAccountActor(traders: Array[Trader]): Unit = {
    for( t <- traders) {
      val clazz = providers.get(t.brokerType).get

      try {
        val c = Class.forName(clazz).newInstance().asInstanceOf[TBrokerage]
        c.accountInfo = t
        context.actorOf(BrokerageActor.props(c),t.id.get.toString())
      }catch  {
        case ex: Throwable => log.error(ex, ex.getMessage())
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

  /**
    *
    * @param trader
    */
  private def _traderCreated(trader: Trader): Unit = {
    val clazz = providers.get(trader.brokerType).get

    try {
      val c = Class.forName(clazz).newInstance().asInstanceOf[TBrokerage]
      c.accountInfo = trader
//      context.actorOf(BrokerageActor.props(c),trader.id.get.toString())
    }catch  {
      case ex: Throwable => log.error(ex, ex.getMessage())
    }
  }

  private def _updateTrader(trader: Trader): Unit = {
    //cache.modifyTrader(trader)
    persisRef ! new UpdateTrader(trader)
  }

  private def _deleteTrader(id: Int): Unit = {
   //cache.removeTrader(id)
    persisRef ! new DeleteTrader(id)
  }

//  private def _getTrader(id: Int): Unit = {
//    sender ! cache.getTrader(id)
//  }


}

object TradeRouteActor {
  def props: Props = {
    Props(classOf[TradeRouteActor])
  }

  val path = "traderRouter"
}

