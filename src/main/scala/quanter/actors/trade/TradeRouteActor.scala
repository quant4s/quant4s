package quanter.actors.trade

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.util.Timeout
import quanter.actors._
import quanter.actors.persistence.PersistenceActor
import quanter.config.Settings
import quanter.interfaces.TBrokerage
import quanter.rest.{HttpServer, Trader}

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
  val restRef = context.actorSelection("/user/" + HttpServer.path)

  val providers = _initProviders()
  var traderCache = new mutable.HashMap[Int, Trader]()

  persisRef ! new ListTraders()

  override def receive: Receive = {
    case t: ListTraders => _listTraders()
    case t: NewTrader => _saveTrader(t.trader)
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
    log.info("接收到回传的TraderList")

    restRef ! traders
    for( t <- traders) {
      traderCache += (t.id.get -> t)

      val clazz = providers.get(t.brokerType).getOrElse("quanter.actors.trade.ctp.CTPBrokerageActor")

      try {
        val c = Class.forName(clazz)
        val brokerageRef = context.actorOf(Props(c), t.id.get.toString)
        brokerageRef ! t
        brokerageRef ! new Connect()
      }catch  {
        case ex: Throwable => log.error(ex, ex.getMessage())
      }
    }
  }

  private def _listTraders(): Unit = {
    log.debug("获取交易账户缓存")
    sender ! traderCache
  }
  private def _saveTrader(trader: Trader): Unit = {
    log.info("创建一个Trader")
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
    persisRef ! new UpdateTrader(trader)
  }

  private def _deleteTrader(id: Int): Unit = {
    persisRef ! new DeleteTrader(id)
  }

}

object TradeRouteActor {
  def props: Props = {
    Props(classOf[TradeRouteActor])
  }

  val path = "traderRouter"
}

