package quanter.actors.data

import akka.actor.{Actor, Props}
import quanter.TimeSpan
import quanter.actors.securities.{SecuritiesManagerActor, SubscriptionSymbol}
import quanter.consolidators.{DataConsolidator, TradeBarConsolidator}
import quanter.data.market.TradeBar
import quanter.indicators.IndicatorDataPoint

/**
  * 000001.XSHE,5,BAR
  */
object BarActor {
  def props (json: String): Props = {
    val arr = json.split(",")
    Props(classOf[BarActor], arr(0), arr(1).toInt)
  }
}

class BarActor(symbol: String, duration: Int) extends Actor {

  val securitiesManagerRef = context.actorSelection("/user/" + SecuritiesManagerActor.Path)
  var consolidator: TradeBarConsolidator = null
  override def receive: Receive = {
    case _ =>   // TODO: 接收到Tick数据
      consolidator.update(null)
  }

  private def _init(): Unit =  {
    consolidator = new TradeBarConsolidator(ptimespan = Some(TimeSpan.fromSeconds(duration)))
    consolidator.dataConsolidated += {(sender, consolidated) => {
      // TODO: 将数据写入到MQ
    }}

    securitiesManagerRef ! new SubscriptionSymbol(symbol)
  }


}