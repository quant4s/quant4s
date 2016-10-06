package quanter.actors.data

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Props}
import quanter.TimeSpan
import quanter.actors.securities.{SecuritiesManagerActor, SubscriptionSymbol}
import quanter.actors.zeromq.{PublishData, ZeroMQServerActor}
import quanter.consolidators.{DataConsolidator, TDataConsolidator, TradeBarConsolidator}
import quanter.data.BaseData
import quanter.data.market.TradeBar
import quanter.indicators.IndicatorDataPoint

import scala.collection.mutable.ArrayBuffer

/**
  * 000001.XSHE,BAR,5
  */
object BarActor {
  def props (json: String): Props = {
    val arr = json.split(",")
    Props(classOf[BarActor], arr(0), arr(2).toInt, json)
  }
}

class BarActor(symbol: String, duration: Int, topic: String) extends BaseIndicatorActor with ActorLogging {
  val _consolidator = _initConsolidator
  var _subscribers = new ArrayBuffer[ActorRef]()
//  val topic = "%s,BAR,%d".format(symbol, duration)

  override def receive: Receive = {
    case data: BaseData =>   // 接收到Tick数据
      _consolidator.update(data)
    case _ => _subscribers += sender
  }

  private def _initConsolidator: TDataConsolidator =  {
    val consolidator = new TradeBarConsolidator(ptimespan = Some(TimeSpan.fromSeconds(duration)))
    consolidator.dataConsolidated += {(sender, consolidated) => {
      log.debug("BAR数据整合,写入到MQ")
      // 写到MQ 的同时， 也可以提交给数据订阅者， 便于指标数据重用
      _subscribers.foreach(s=> s ! consolidated)
      pubRef ! PublishData(topic, consolidated.toJson)
    }}

    securitiesManagerRef ! new SubscriptionSymbol(symbol)
    consolidator
  }


}