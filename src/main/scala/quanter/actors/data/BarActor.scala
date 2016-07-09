package quanter.actors.data

import akka.actor.{Actor, Props}
import quanter.TimeSpan
import quanter.actors.securities.{SecuritiesManagerActor, SubscriptionSymbol}
import quanter.consolidators.{DataConsolidator, TDataConsolidator, TradeBarConsolidator}
import quanter.data.BaseData
import quanter.data.market.TradeBar
import quanter.indicators.IndicatorDataPoint

/**
  * 000001.XSHE,BAR,5
  */
object BarActor {
  def props (json: String): Props = {
    val arr = json.split(",")
    Props(classOf[BarActor], arr(0), arr(2).toInt)
  }
}

class BarActor(symbol: String, duration: Int) extends Actor {

  val securitiesManagerRef = context.actorSelection("/user/" + SecuritiesManagerActor.path)
  val _consolidator = _initConsolidator
  override def receive: Receive = {
    case data: BaseData =>   // TODO: 接收到Tick数据
      _consolidator.update(data)
  }

  private def _initConsolidator: TDataConsolidator =  {
    val consolidator = new TradeBarConsolidator(ptimespan = Some(TimeSpan.fromSeconds(duration)))
    consolidator.dataConsolidated += {(sender, consolidated) => {
      // TODO: 将数据写入到MQ 或者WS
    }}

    securitiesManagerRef ! new SubscriptionSymbol(symbol)
    consolidator
  }


}