package quanter.actors.trade

import akka.actor.{Actor, ActorLogging, Props}
import quanter.interfaces.TBrokerage
import quanter.persistence.EOrder
import quanter.strategies.StrategyCache

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
    // TODO: 连接服务器
    // TODO: 每间隔3s 刷新, 检测状态，保持交易通道连接
  }

  override def receive: Receive = {
    case o: EOrder => _handleOrder(o)
  }

  private def _handleOrder(order: EOrder): Unit = {
//    if(order.side == 0) {
//      brokerage.buy(order.symbol, order.quantity, order.price)
//    } else if(order.side == 1) {
//      brokerage.sell(order.symbol, order.quantity, order.price)
//    }
  }

  private def _refresh(): Unit = {
    brokerage.keep
  }
}
