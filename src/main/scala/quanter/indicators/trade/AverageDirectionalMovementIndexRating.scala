package quanter.indicators.trade

import quanter.data.market.TradeBar
import quanter.indicators.TradeBarIndicator
import quanter.indicators.window.RollingWindow

/**
  *
  */
class AverageDirectionalMovementIndexRating(pname: String, pperiod: Int) extends TradeBarIndicator(pname) {
  private val _period: Int = pperiod
  private val  _adx: AverageDirectionalIndex = new AverageDirectionalIndex(name + "_ADX", pperiod)
  private val _adxHistory: RollingWindow[Double] = new RollingWindow[Double](pperiod)

  def this(pperiod: Int) {
    this("ADXR" + pperiod, pperiod)
  }

  override def isReady: Boolean = samples >= _period

  override def computeNextValue(input: TradeBar): Double = {
    _adx.update(input)
    _adxHistory.add(_adx.current.value)

  (_adx.current.value + _adxHistory.get(math.min(_adxHistory.count - 1, _period - 1))) / 2
  }

  override def reset: Unit = {
    _adx.reset
    _adxHistory.reset
    super.reset
  }
}
