package quanter.indicators

import quanter.indicators.IndicatorExtensions._
import quanter.indicators.MovingAverageType.MovingAverageType

/**
  *
  */
class MovingAverageConvergenceDivergence(name: String, fastPeriod: Int, slowPeriod: Int, signalPeriod: Int, maType: MovingAverageType = MovingAverageType.Simple) extends Indicator(name)  {

  val fast: IndicatorBase[IndicatorDataPoint] = maType.asIndicator(name + "_FAST", fastPeriod)
  val slow: IndicatorBase[IndicatorDataPoint] = maType.asIndicator(name + "_SLOW", slowPeriod)
  val signal: IndicatorBase[IndicatorDataPoint] = maType.asIndicator(name + "_SIGNAL", signalPeriod)

  def this(fastPeriod: Int, slowPeriod: Int, signalPeriod: Int, maType: MovingAverageType) {
    this("MACD(%d,%d)".format(fastPeriod, slowPeriod), fastPeriod, slowPeriod, signalPeriod, maType)
  }

  override def isReady = signal.isReady

  override def reset = {
    fast.reset
    slow.reset
    signal.reset
    super.reset
  }

  override def computeNextValue(input: IndicatorDataPoint) = {
    log.debug("macd: computeNextValue%s, %s".format(input.time.toString(), input.value.toString()))
    fast.update(input)
    slow.update(input)

    val macd = fast - slow
    if (fast.isReady && slow.isReady) signal.update(input.time, macd)
    log.debug("fast's value: %s, slow's value: %s".format(fast.current.value.toString(), slow.current.value.toString()))
    macd
  }

  override def toJson = {
    "{\"fast\": ${fast.current.value}, \"slow\": ${slow.current.value}, \"signal\": ${signal.current.value}}"
  }

}
