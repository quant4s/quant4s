package quanter.actors.data

import akka.actor.{ActorLogging, ActorRef, Props}
import quanter.TimeSpan
import quanter.actors.securities.SubscriptionSymbol
import quanter.actors.zeromq.PublishData
import quanter.consolidators.{TDataConsolidator, TradeBarConsolidator}
import quanter.data.BaseData
import org.quant4s.data.market.TradeBar
import quanter.indicators._
import quanter.indicators.composite.DoubleMovingAverageIndex
import quanter.indicators.window.{ExponentialMovingAverage, SimpleMovingAverage}

import scala.collection.mutable.ArrayBuffer

/**
  * 指标Actor
  * 000001.XSHE,MACD,5,9|9|21
  */

object IndicatorActor {
  def props(symbol: String, duration: Int, indiName: String, param: String, topic: String) = {
    Props(classOf[IndicatorActor], symbol, duration, indiName, param, topic)
  }

  def props(symbol: String, duration: Int, indiName: String, param: String) = {
    Props(classOf[IndicatorActor], symbol, duration, indiName, param)
  }

  val splitChar = "~"

  // DATAPOINT INDICATOR
  val MACD = "MACD"
  val EMA = "EMA"
  val SMA = "SMA"
  val RSI = "RSI"
  val SAR = "SAR"
  val KDJ = "KDJ"
  val DMI = "DMI"
  val KAMA = "KAMA"

  // TRADEBAR INDICATOR
  val PSAR = "PSAR"
  val MFI = "MFI"
  val OBV = "OBV"

}

/**
  * 创建一个指标，指标有多种
  *
  * @param symbol
  * @param duration
  * @param name
  * @param param
  * @param topic
  */
class IndicatorActor(symbol: String, duration: Int, name: String, param: String, topic: String) extends BaseIndicatorActor with ActorLogging {

  def this(symbol: String, duration: Int, name: String, param: String) {
    this(symbol, duration, name, param, "")
  }
//  val _consolidator = _init()
  var _subscribers = new ArrayBuffer[ActorRef]()
  val _consolidator = _resolveConsolidators(symbol, duration)

  _init()


  override def receive: Receive = {
    case data: BaseData => _consolidator.update(data)  // 计算指标
    case _ => _subscribers += sender
  }


  /**
    * 初始化指标, TODO: 用型变处理泛型化继承
    */
  private def _init(): Unit = {
    log.info("初始化技术指标{name: %s}".format(name))
    val params = param.split(IndicatorActor.splitChar)
    name match {
      case IndicatorActor.MACD => _macd(params(0).toInt, params(1).toInt, params(2).toInt)
      case IndicatorActor.SMA => _sma(params(0).toInt)
      case IndicatorActor.EMA => _ema(params(0).toInt)
      case IndicatorActor.KAMA => _kama(params(0).toInt)
      case IndicatorActor.RSI => _rsi(params(0).toInt)
    }

    securitiesManagerRef ! new SubscriptionSymbol(symbol)

//    val indicator = new IndicatorFactory().createDataPointIndicator(name, param)
//    val consolidator = _resolveConsolidators(symbol, duration)
//
//    _registerDataPointIndicator(symbol, indicator, consolidator)
//
//    securitiesManagerRef ! new SubscriptionSymbol(symbol)
//    consolidator
  }

  private def _resolveConsolidators(symbol: String, duration: Int): TDataConsolidator = {
    new TradeBarConsolidator(ptimespan = Some(TimeSpan.fromSeconds(duration)))
  }

  private def _registerDataPointIndicator(symbol: String, indicator: IndicatorBase[IndicatorDataPoint],
                                          consolidator: TDataConsolidator,
                                          selector: BaseData => IndicatorDataPoint = {x: BaseData => new IndicatorDataPoint(x.symbol, x.endTime, x.value)} ): Unit = {
//    consolidator.dataConsolidated += {(sender, consolidated) => {
//      val value = ts(consolidated)
//      indicator.update(value)
//      log.debug("%s指标数据写入到MQ".format(topic))
//      // 写到MQ 的同时， 也可以提交给数据订阅者， 便于指标数据重用
//      _subscribers.foreach(s=> s ! indicator)
//      pubRef ! PublishData(topic, indicator.toJson)
//    }}

    _registerIndicator(symbol, indicator, consolidator, selector)
  }

  private def _registerTradeBarIndicator(symbol: String, indicator: IndicatorBase[TradeBar], consolidator: TDataConsolidator, selector: BaseData => TradeBar ) = {
//    consolidator.dataConsolidated += {(sender, consolidated) => {
//      val value = selector(consolidated)
//      indicator.update(value)
//      log.debug("%s指标数据写入到MQ".format(topic))
//      pubRef ! PublishData(topic, indicator.toJson)
//    }}
    _registerIndicator(symbol, indicator, consolidator, selector)

  }

  private def _registerIndicator[T <: BaseData](symbol: String, indicator: IndicatorBase[T], consolidator: TDataConsolidator, selector: BaseData => T ): Unit = {
    consolidator.dataConsolidated += {(sender, consolidated) => {
      val value = selector(consolidated)
      indicator.update(value)
      log.debug("%s指标数据写入到MQ".format(topic))
      if(topic.length != 0)
        pubRef ! PublishData(topic, indicator.toJson)

      _subscribers.foreach( ref => ref ! indicator.toJson)
    }}
  }


  //=========================================== 创建指标开始 =======================================
  private def _macd(fastPeriod: Int, slowPeriod: Int, signalPeriod: Int) = {
    // val name = _createIndicatorName(symbol, "MACD{%d,%d}".format(fastPeriod, slowPeriod), duration)
    val macd = new MovingAverageConvergenceDivergence(fastPeriod, slowPeriod, signalPeriod, MovingAverageType.Simple)
    _registerDataPointIndicator(symbol, macd, _consolidator)
  }

  private def _mfi(period: Int) = {
    val mfi = new MoneyFlowIndex(period)
    _registerTradeBarIndicator(symbol, mfi, _consolidator, x => x.asInstanceOf[TradeBar])
  }

  private def _sma(period: Int) = {
    val sma = new SimpleMovingAverage(period)
    _registerDataPointIndicator(symbol, sma, _consolidator)
  }

  private def _ema(period: Int) = {
    val ema = new ExponentialMovingAverage(period)
    _registerDataPointIndicator(symbol, ema, _consolidator)
  }

  private def _kama(period: Int) = {
    val kama = new KaufmanAdaptiveMovingAverage(period)
    _registerDataPointIndicator(symbol, kama, _consolidator)
  }

  private def _rsi(period: Int) = {
    val rsi = new RelativeStrengthIndex(period)
    _registerDataPointIndicator(symbol, rsi, _consolidator)
  }

  private def _psar(afStart: Double, afIncrement: Double, afMax: Double) = {
    val psar = new ParabolicStopAndReverse(afStart, afIncrement, afMax)
    _registerTradeBarIndicator(symbol, psar, _consolidator, {x => x.asInstanceOf[TradeBar]})
  }


  private def _obv() = {
    val obv = new OnBalanceVolume("OBV")
    _registerTradeBarIndicator(symbol, obv, _consolidator, {x => x.asInstanceOf[TradeBar]})
  }

  private def _dmai() =  {
    val dmai = new DoubleMovingAverageIndex(5, 15)
    _registerDataPointIndicator(symbol, dmai, _consolidator)
  }

}
