package quanter.actors.trade

import akka.actor.{Actor, ActorLogging, Props}
import quanter.persistence.EOrder
import quanter.strategies.StrategyCache
import quanter.trade.TradeAccount

class TradersManager() {

}
/**
  *
  */
object TradeActor {
  def props(trade: TradeAccount): Props = {
    Props(classOf[TradeActor], trade)
  }
}
class TradeActor(trade: TradeAccount) extends Actor with ActorLogging{
//  val managers = new TradersManager()

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    // TODO: 每间隔3s 刷新, 保持交易通道连接
  }

  override def receive: Receive = {
    case o: EOrder => _handleOrder(o)
  }

  private def _handleOrder(order: EOrder): Unit = {
    if(order.side == 0) {
      trade.buy(order.symbol, order.quantity, order.price)
    } else if(order.side == 1) {
      trade.sell(order.symbol, order.quantity, order.price)
    }
  }

  private def _refresh(): Unit = {

  }
}
