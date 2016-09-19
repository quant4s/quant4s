package quanter.actors.trade

import akka.actor.{Actor, ActorLogging, FSM, Props}
import quanter.actors.trade.BrokerageActor._
import quanter.actors.{Connect, Disconnect, KeepAlive}
import quanter.interfaces.TBrokerage
import quanter.persistence.EOrder

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


/**
  *
  */


class BrokerageActor(brokerage: TBrokerage) extends FSM[BrokerageState, BrokerageData] with ActorLogging{

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    // 每间隔3s 刷新, 检测状态，保持交易通道连接
    context.system.scheduler.schedule(0 seconds, 3 seconds, self, new KeepAlive())
  }

  when(Login) {
    case Event(_, _) =>
      stay()
  }
  when(Connected) {
    case Event(Disconnect, _) =>
      _disconnect()
      goto(Disconnected)
    case Event(o: EOrder, _) =>
      _handleOrder(o)
      stay()
    case Event(KeepAlive, _) =>
      _refresh()
      stay()
  }
  when(Disconnected) {
    case Event(Connected, _) =>
      _connect()
      goto(Connected)
  }

  private def _handleOrder(order: EOrder): Unit = {
    if(order.side == 0) {
      brokerage.buy(order.symbol, order.price, order.quantity)
    } else if(order.side == 1) {
      brokerage.sell(order.symbol, order.price, order.quantity)
    }
  }

  private def _refresh(): Unit = {
    brokerage.keep
  }

  private def _connect(): Unit = {
    brokerage.connect
  }

  private def _disconnect(): Unit = {
    brokerage.disconnect
  }
}

object BrokerageActor {
  def props(brokerage: TBrokerage): Props = {
    Props(classOf[BrokerageActor], brokerage)
  }

  sealed trait BrokerageState

  case object Connected extends BrokerageState
  case object Disconnected extends BrokerageState
  case object Login extends BrokerageState

  case class BrokerageData()
}