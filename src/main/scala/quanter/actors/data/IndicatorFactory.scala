package quanter.actors.data


import quanter.indicators.{IndicatorDataPoint, _}
import quanter.indicators.window.{ExponentialMovingAverage, SimpleMovingAverage}


/**
  *
  */
class IndicatorFactory {

  def createIndicator(name: String, param: String): IndicatorBase[IndicatorDataPoint] = {
    var indicator: IndicatorBase[IndicatorDataPoint] = null
    name match {
      case IndicatorFactory.MACD =>
        val params = param.split(IndicatorFactory.regex)
        indicator = _macd(params(0).toInt, params(1).toInt, params(2).toInt)
      case IndicatorFactory.SMA =>
        val params = param.split(IndicatorFactory.regex)
        indicator = _sma(params(0).toInt)
      case IndicatorFactory.EMA =>
        val params = param.split(IndicatorFactory.regex)
        indicator = _ema(params(0).toInt)
      case IndicatorFactory.KAMA =>
        val params = param.split(IndicatorFactory.regex)
        indicator = _kama(params(0).toInt)
      case IndicatorFactory.PSAR =>
        val params = param.split(IndicatorFactory.regex)
         //indicator = _psar(params(0).toDouble, params(1).toDouble, params(2).toDouble)

      case _ =>
    }

    indicator
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
  private val regex = "~"

  val MACD = "MACD"
  val EMA = "EMA"
  val SMA = "SMA"
  val RSI = "RSI"
  val PSAR = "PSAR"
  val SAR = "SAR"
  val KDJ = "KDJ"
  val DMI = "DMI"
  val KAMA = "KAMA"

}
