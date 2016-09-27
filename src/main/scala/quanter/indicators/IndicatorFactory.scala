package quanter.indicators

import quanter.data.market.TradeBar
import quanter.indicators.window.{ExponentialMovingAverage, SimpleMovingAverage}


/**
  *
  */
class IndicatorFactory {

  def createDataPointIndicator(name: String, param: String): IndicatorBase[IndicatorDataPoint] = {
//    var indicator: IndicatorBase[IndicatorDataPoint] = null
    name match {
      case IndicatorFactory.MACD =>
        val params = param.split(IndicatorFactory.splitChar)
        _macd(params(0).toInt, params(1).toInt, params(2).toInt)
      case IndicatorFactory.SMA =>
        val params = param.split(IndicatorFactory.splitChar)
        _sma(params(0).toInt)
      case IndicatorFactory.EMA =>
        val params = param.split(IndicatorFactory.splitChar)
        _ema(params(0).toInt)
      case IndicatorFactory.KAMA =>
        val params = param.split(IndicatorFactory.splitChar)
        _kama(params(0).toInt)

      case _ => null
    }

  }

  def createTradeBarIndicator(name: String, param: String) : TradeBarIndicator = {
    name match {
      case IndicatorFactory.PSAR =>
        val params = param.split(IndicatorFactory.splitChar)
        _psar(params(0).toDouble, params(1).toDouble, params(2).toDouble)
      case _ => null
    }
  }

  private def _macd(fastPeriod: Int, slowPeriod: Int, signalPeriod: Int) = {
    new MovingAverageConvergenceDivergence(fastPeriod, slowPeriod, signalPeriod, MovingAverageType.Simple)
  }

  private def _sma(period: Int) = {
    new SimpleMovingAverage(period)
  }

  private def _ema(period: Int) = {
    new ExponentialMovingAverage(period)
  }

  private def _kama(period: Int) = {
    new KaufmanAdaptiveMovingAverage(period)
  }

  private def _psar(afStart: Double, afIncrement: Double, afMax: Double) = {
    new ParabolicStopAndReverse(afStart, afIncrement, afMax)
  }
}

object IndicatorFactory {
  private val splitChar = "~"

  val MACD = "MACD"
  val EMA = "EMA"
  val SMA = "SMA"
  val RSI = "RSI"
  val SAR = "SAR"
  val KDJ = "KDJ"
  val DMI = "DMI"
  val KAMA = "KAMA"

  val PSAR = "PSAR"

}
