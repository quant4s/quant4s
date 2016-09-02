package quanter.actors.data

import quanter.indicators.{Indicator, MovingAverageConvergenceDivergence, MovingAverageType}
import quanter.indicators.MovingAverageType.MovingAverageType
import quanter.indicators.window.{ExponentialMovingAverage, SimpleMovingAverage}

/**
  *
  */
class IndicatorFactory {

  def createIndicator(name: String, param: String): Indicator = {
    var indicator: Indicator = null
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
      case _ =>
    }

    indicator
  }

  private def _macd(fastPeriod: Int, slowPeriod: Int, signalPeriod: Int): Indicator = {
    new MovingAverageConvergenceDivergence(fastPeriod, slowPeriod, signalPeriod, MovingAverageType.Simple)
  }

  private def _sma(period: Int): Indicator = {
    new SimpleMovingAverage(period)
  }

  private def _ema(period: Int): Indicator = {
    new ExponentialMovingAverage(period)
  }
}

object IndicatorFactory {
  private val regex = "~"

  val MACD = "MACD"
  val EMA = "EMA"
  val SMA = "SMA"
  val RSI = "RSI"
}
