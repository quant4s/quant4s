package quanter.actors.data

import quanter.indicators.{Indicator, MovingAverageConvergenceDivergence, MovingAverageType}
import quanter.indicators.MovingAverageType.MovingAverageType

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
      case _ =>
    }

    indicator
  }

  private def _macd(fastPeriod: Int, slowPeriod: Int, signalPeriod: Int): Indicator = {
    new MovingAverageConvergenceDivergence(fastPeriod, slowPeriod, signalPeriod, MovingAverageType.Simple)
  }
}

object IndicatorFactory {
  private val regex = "|"

  val MACD = "macd"
  val MA = "ma"
}
