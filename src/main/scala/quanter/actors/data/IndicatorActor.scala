package quanter.actors.data

import akka.actor.{Actor, ActorLogging, Props}
import quanter.TimeSpan
import quanter.actors.securities.{SecuritiesManagerActor, SubscriptionSymbol}
import quanter.actors.zeromq.{PublishData, ZeroMQServerActor}
import quanter.consolidators.{TDataConsolidator, TradeBarConsolidator}
import quanter.data.BaseData
import quanter.data.market.TradeBar
import quanter.indicators.{Indicator, IndicatorBase, IndicatorDataPoint}

/**
  * 指标Actor
  * 000001.XSHE,MACD,5,9|9|21
  */

object IndicatorActor {
  def props (json: String): Props = {
    val arr = json.split(",")
    val symbol = arr(0)
    val duration = arr(2).toInt
    val name = arr(1)
    val param = arr(3)

    Props(classOf[IndicatorActor], symbol, duration, name, param, json)
  }
}

class IndicatorActor(symbol: String, duration: Int, name: String, param: String, topic: String) extends Actor with ActorLogging {
  type SelectType = (BaseData) => Double
  val securitiesManagerRef = context.actorSelection("/user/" + SecuritiesManagerActor.path)
  val _consolidator = _initIndicator
  val pubRef = context.actorSelection("/user/" + ZeroMQServerActor.path)


  override def receive: Receive = {
    case data: BaseData => _consolidator.update(data)  // 计算指标

  }

  /**
    * 初始化指标
    */
  private def _initIndicator : TDataConsolidator[TradeBar] = {

    val indicator = new IndicatorFactory().createIndicator(name, param)
    val consolidator = _resolveConsolidators(symbol, duration)

    _registerIndicator(symbol, indicator, consolidator)

    securitiesManagerRef ! new SubscriptionSymbol(symbol)
    consolidator
  }

  private def _resolveConsolidators(symbol: String, duration: Int): TDataConsolidator = {
    new TradeBarConsolidator(ptimespan = Some(TimeSpan.fromSeconds(duration)))
  }

  private def _registerIndicator(symbol: String, indicator: IndicatorBase[IndicatorDataPoint], consolidator: TDataConsolidator) = {
    val ts: SelectType = { x => x.value}
    consolidator.dataConsolidated += {(sender, consolidated) => {
      val value = ts(consolidated)
      indicator.update(new IndicatorDataPoint(consolidated.symbol, consolidated.endTime, value))
      // TODO: 将数据写入到MQ 或者WS
      log.debug("指标数据整合,写入到WS")
      pubRef ! PublishData(topic, consolidated.toJson)

    }}

    //    subscriptionManager.addConsolidator(symbol, consolidator)
  }


}
