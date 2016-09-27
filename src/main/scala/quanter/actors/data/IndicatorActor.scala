package quanter.actors.data

import akka.actor.{ActorLogging, Props}
import quanter.TimeSpan
import quanter.actors.securities.SubscriptionSymbol
import quanter.actors.zeromq.PublishData
import quanter.consolidators.{TDataConsolidator, TradeBarConsolidator}
import quanter.data.BaseData
import quanter.data.market.TradeBar
import quanter.indicators.{IndicatorBase, IndicatorDataPoint, IndicatorFactory}

/**
  * 指标Actor
  * 000001.XSHE,MACD,5,9|9|21
  */

object IndicatorActor {
  def props(symbol: String, duration: Int, indiName: String, param: String, topic: String) = {
    Props(classOf[IndicatorActor], symbol, duration, indiName, param, topic)
  }
//  def props (json: String): Props = {
//    val arr = json.split(",")
//    val symbol = arr(0)
//    val duration = arr(2).toInt
//    val name = arr(1)
//    val param = arr(3)
//
//    Props(classOf[IndicatorActor], symbol, duration, name, param, json)
//  }
}

class IndicatorActor(symbol: String, duration: Int, name: String, param: String, topic: String) extends BaseIndicatorActor with ActorLogging {
  val _consolidator = _initIndicator


  override def receive: Receive = {
    case data: BaseData => _consolidator.update(data)  // 计算指标
  }

  /**
    * 初始化指标, TODO: 用型变处理泛型化继承
    */
  private def _initIndicator : TDataConsolidator = {

    val indicator = new IndicatorFactory().createDataPointIndicator(name, param)
    val consolidator = _resolveConsolidators(symbol, duration)

    _registerDataPointIndicator(symbol, indicator, consolidator)
    _registerTradeBarIndicator[TradeBar](symbol, null, consolidator, {x => x.asInstanceOf[TradeBar]})

    securitiesManagerRef ! new SubscriptionSymbol(symbol)
    consolidator
  }

  private def _resolveConsolidators(symbol: String, duration: Int): TDataConsolidator = {
    new TradeBarConsolidator(ptimespan = Some(TimeSpan.fromSeconds(duration)))
  }

  private def _registerDataPointIndicator(symbol: String, indicator: IndicatorBase[IndicatorDataPoint], consolidator: TDataConsolidator, ts: BaseData => Double = {x => x.value} ) = {
    consolidator.dataConsolidated += {(sender, consolidated) => {
      val value = ts(consolidated)
      indicator.update(new IndicatorDataPoint(consolidated.symbol, consolidated.endTime, value))
      log.debug("%s指标数据写入到MQ".format(topic))
      pubRef ! PublishData(topic, indicator.toJson)
    }}

    //    subscriptionManager.addConsolidator(symbol, consolidator)
  }


  private def _registerTradeBarIndicator[T <: BaseData](symbol: String, indicator: IndicatorBase[T], consolidator: TDataConsolidator, selector: BaseData => T ) = {
    consolidator.dataConsolidated += {(sender, consolidated) => {
      val value = selector(consolidated)
      indicator.update(value)
      log.debug("%s指标数据写入到MQ".format(topic))
      pubRef ! PublishData(topic, indicator.toJson)

    }}
  }
}
