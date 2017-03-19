package quanter.actors.trade

import akka.actor.{ActorLogging, FSM, Props}
import quanter.actors.{ConnectedSuccess, _}
import quanter.actors.trade.BrokerageActor._
import quanter.actors.trade.TradeAccountEvent.TradeAccountEvent
import quanter.interfaces.TBrokerage
import quanter.rest.{Order, TradeAccount}

import scala.collection.mutable.ArrayBuffer


/**
  *
  */
abstract class BrokerageActor(/*brokerage: TBrokerage*/) extends FSM[BrokerageState, BrokerageData] with Trader with ActorLogging{
  var accountInfo: TradeAccount = null
  var connectNum = 0

  protected var _isConnected = false
  def isConnected = _isConnected

  protected var _isLogin = false
  def isLogin = _isLogin

  startWith(Initialized, new BrokerageData())
  when(Initialized) {
    case Event(a: TradeAccount, _) => {
      log.debug("赋值交易账号")
      accountInfo = a
      stay
    }
    case Event(Connect(), _)=> {
      log.debug("[when(Initialized) Event(Connect(), _)) Event]开始连接交易接口")
      connect
      stay
    }
    case Event(ConnectedSuccess(), _) => {
      log.debug("开始登录")
      login()
      goto(Connected)
    }
  }
  when(Connected) {
    case Event(LoginSuccess(), _) =>
      log.debug("登录交易接口成功")
      goto(LoggedIn)
  }
  when(LoggedIn) {
    case Event(KeepAlive(), _) => {
      _refresh()
      stay()
    }
    case Event(Disconnect(), _) => {
      log.debug("断开交易接口连接")
      _disconnect()
      goto(Disconnected)
    }
    case Event(o: Order, _) => {
      log.debug("接受到委托单")
      _handleOrder(o)
      stay()
    }
  }
  when(Disconnected) {
    case Event(Connected, _) => {
      log.debug("重新连接")
      connect()
      goto(Connected)
    }
  }
  whenUnhandled {
    case Event(o:Order, _) => {
      log.warning("未登录时，有委托单需要处理，暂存等待连接时处理")
      pendOrder(o)
      stay()
    }
  }
  onTransition {
    case Connected -> LoggedIn => // 当状态转移到登录以后， 自动处理未处理的订单
      log.debug("登录成功，自动执行")
      restoreOrders()
  }

  private var _pendingOrders = ArrayBuffer[Order]()
  private def pendOrder(o: Order): Unit = {
    _pendingOrders += o
  }

  private def restoreOrders(): Unit = {
    _pendingOrders.foreach( o => {
      log.warning("自动处理未处理的订单")
      self ! o
    })

  }

  private def _handleOrder(o: Order): Unit = {
    order(o)
//    if(order.side == 0) {
//      //brokerage.buy(order.symbol, order.price, order.quantity)
//    } else if(order.side == 1) {
//      //brokerage.sell(order.symbol, order.price, order.quantity)
//    }
  }

  private def _refresh(): Unit = {
    //brokerage.keep
  }

  protected def connect(): Unit = self ! new ConnectedSuccess()

  protected def login(): Unit = self ! new LoginSuccess()

  protected def logout(): Unit = {}

  private def _disconnect(): Unit = {
    //brokerage.disconnect
  }

  /**
    * 通知其他Actor账户的事件， 主要是为了通知到UI，显示给用户
    */
  def fireEvent(event: TradeAccountEvent): Unit = {
    new TradeAccountMessage(event, accountInfo.id.get)
  }

  var _nextResultId = 1
  def nextResultId: String = {
    _nextResultId += 1
    _nextResultId.toString
  }

  var _nextRequestId = 1
  def nextRequestId = {
    _nextRequestId += 1
    _nextRequestId
  }

  def getStrategyId(localId: String): String = {
    localId.take(localId.indexOf("-"))
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
  case object LoggedIn extends BrokerageState

  case class BrokerageData()
}