package quanter.actors.trade

import akka.actor.{Actor, ActorLogging, FSM, Props}
import quanter.actors.trade.BrokerageActor._
import quanter.actors.trade.TradeAccountEvent.TradeAccountEvent
import quanter.actors.{Connect, Disconnect, KeepAlive}
import quanter.interfaces.TBrokerage
import quanter.persistence.EOrder
import quanter.rest.Trader

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


/**
  *
  */


abstract class BrokerageActor(/*brokerage: TBrokerage*/) extends FSM[BrokerageState, BrokerageData] with ActorLogging{

  var accountInfo: Trader = null

  protected var _isConnected = false
  def isConnected = _isConnected

  protected var _logined = false
  def isLogined = _isConnected

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    // 每间隔3s 刷新, 检测状态，保持交易通道连接
    context.system.scheduler.schedule(0 seconds, 3 seconds, self, new KeepAlive())
  }

  startWith(Initialized, new BrokerageData())
  when(Initialized) {
    case Event(Connect(), _) => {
      _connect()
      goto(Connected)
    }
    case Event(KeepAlive(), _)=> {
      log.debug("不支持keep alive")
      stay()
    }
  }
  when(Connected) {
    case Event(Disconnect(), _) =>
      _disconnect()
      goto(Disconnected)
    case Event(o: EOrder, _) =>
      _handleOrder(o)
      stay()
    case Event(KeepAlive(), _) =>
      _refresh()
      stay()
  }
  when(Disconnected) {
    case Event(Connected, _) =>
      _connect()
      goto(Connected)
    case Event(KeepAlive, _)=> stay()
  }

  private def _handleOrder(order: EOrder): Unit = {
    if(order.side == 0) {
      //brokerage.buy(order.symbol, order.price, order.quantity)
    } else if(order.side == 1) {
      //brokerage.sell(order.symbol, order.price, order.quantity)
    }
  }

  private def _refresh(): Unit = {
    //brokerage.keep
  }

  private def _connect(): Unit = {
    log.info("连接交易帐号")
  }

  private def _disconnect(): Unit = {
    //brokerage.disconnect
  }

  /**
    * 通知其他Actor账户的事件， 主要是为了通知到UI，显示给用户
    */
  def fireEvent(event: TradeAccountEvent): Unit = {
    new TradeAccountMessage(event, accountInfo.id.get)
  }

  /**
    * 回报到达处理
    */
  def fireResp(): Unit = {
    // TODO: 保存到数据库
  }
}

object BrokerageActor {
  def props(brokerage: TBrokerage): Props = {
    Props(classOf[BrokerageActor], brokerage)
  }

  sealed trait BrokerageState

  case object Initialized extends BrokerageState
  case object Connected extends BrokerageState
  case object Disconnected extends BrokerageState
  case object Login extends BrokerageState

  case class BrokerageData()
}