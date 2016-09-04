package quanter.actors.trade

import akka.actor.{Actor, ActorLogging, Props}
import quanter.actors.{Connect, Disconnect, KeepAlive}
import quanter.interfaces.TBrokerage
import quanter.persistence.EOrder

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


/**
  *
  */
object BrokerageActor {
  def props(brokerage: TBrokerage): Props = {
    Props(classOf[BrokerageActor], brokerage)
  }
}

class BrokerageActor(brokerage: TBrokerage) extends Actor with ActorLogging{

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    // 每间隔3s 刷新, 检测状态，保持交易通道连接
    context.system.scheduler.schedule(0 seconds, 3 seconds, self, new KeepAlive())
  }

  override def receive: Receive = {
    case o: EOrder => _handleOrder(o)
    case KeepAlive => _refresh
    case Connect => _connect
    case Disconnect => _disconnect
  }

  private def _handleOrder(order: EOrder): Unit = {
    if(order.side == 0) {
      brokerage.buy(order.symbol, order.price, order.quantity)
    } else if(order.side == 1) {
      brokerage.sell(order.symbol, order.price, order.quantity)
    }
  }

  private def _refresh: Unit = {
    brokerage.keep
  }

  private def _connect: Unit = {
    brokerage.connect
  }

  private def _disconnect: Unit = {
    brokerage.disconnect
  }
}
