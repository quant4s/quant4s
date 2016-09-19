package quanter.actors.trade

import java.util.HashMap

import scala.collection.mutable
import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import quanter.actors._
import quanter.actors.persistence.PersistenceActor
import quanter.actors.provider.QuerySnapData
import quanter.brokerages.ctp.CTPBrokerage
import quanter.config.Settings
import quanter.interfaces.TBrokerage
import quanter.persistence.EOrder
import quanter.rest.{Trader, Transaction}
import quanter.trade.TradeAccountCache
import quanter.trade.simulate.SimulateBrokerage

import scala.concurrent.Await
import scala.reflect._
import scala.reflect.runtime._


/**
  * 1、管理交易接口
  * 2、处理订单
  */
case class InitTradeRoute()
class TradeRouteActor extends Actor with ActorLogging{
//  var traderAccounts = new mutable.HashMap[Int, ActorRef]()
//  val cache = new TradeAccountCache()
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
//    case t: GetTrader => _getTrader(t.id)

//    case tran: Transaction => _handleOrder(tran)
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
    val setting = Settings(context.system)
    val map = new mutable.HashMap[String, String]()
    for(i <- 0 until setting.channelTypes.size()) {
      val provider = setting.channelTypes.get(i).asInstanceOf[java.util.HashMap[String, String]]
      map.put(provider.get("name"), provider.get("driver"))
    }

    for(t <- result) {
      val clazz = map.get(t.brokerType).toString

      val c = Class.forName(clazz)
      //TODO: 动态实例化对象

      //val ref = context.actorOf(Props(Class.forName(clazz)), path)
//       t.brokerType match {
//        case "THS" => log.info("启动同花顺交易接口")
//        case "CTP" => log.info("启动CTP交易接口")
//          val brokerage = new CTPBrokerage(t.name)
//          val ref = context.actorOf(BrokerageActor.props(brokerage))
////          traderAccounts += (t.id.get -> ref)
//
//        case "SIM" => {
//           val brokerage = new SimulateBrokerage(t.name)
//           val ref = context.actorOf(BrokerageActor.props(brokerage))
////          traderAccounts += (t.id.get -> ref)
//        }
//        case _ => log.info("启动监控")
//      }

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

