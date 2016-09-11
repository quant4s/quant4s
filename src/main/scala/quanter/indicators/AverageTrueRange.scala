/**
  *
  */
package quanter.indicators

import quanter.data.market.TradeBar
import quanter.indicators.MovingAverageType.MovingAverageType
import quanter.indicators.IndicatorExtensions._

/**
  *
  */
class AverageTrueRange(pname: String, pperiod: Int, movingAverageType: MovingAverageType) extends TradeBarIndicator(pname) {

  def this(pname: String, pperiod: Int) {
    this(pname, pperiod, MovingAverageType.Wilders)
  }

  def this(pperiod: Int, pmovingAverageType: MovingAverageType = MovingAverageType.Wilders) {
    this("ATR" + pperiod, pperiod, pmovingAverageType)
  }

  private val  _smoother: IndicatorBase[IndicatorDataPoint] = movingAverageType.asIndicator("%s_%s".format(name, movingAverageType), pperiod)
  var previous: TradeBar = null

  val trueRange: IndicatorBase[TradeBar] = new FunctionalIndicator[TradeBar](pname + "_TrueRange",currentBar => {
    val nextValue = computeTrueRange(previous, currentBar)
    previous = currentBar
    nextValue
  }, trueRangeIndicator => trueRangeIndicator.samples >= 1)

  override def reset: Unit = {
    _smoother.reset
    trueRange.reset
    super.reset
  }

  override def isReady: Boolean = _smoother.isReady

  override def computeNextValue(input: TradeBar): Double = {
    trueRange.update(input)
    _smoother.update(input.time, trueRange.current.value)

    _smoother.current.value
  }

  def computeTrueRange( previous: TradeBar, current: TradeBar): Double = {
    val range1 = current.high - current.low
    if (previous == null) range1
    else {
      val range2 = math.abs(current.high - previous.close)
      val range3 = math.abs(current.low - previous.close)

      math.max(range1, math.max(range2, range3))
    }
  }
}
