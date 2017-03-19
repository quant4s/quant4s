package quanter.indicators.trade

import org.quant4s.data.market.TradeBar
import quanter.indicators.TradeBarIndicator
import quanter.indicators.window.ExponentialMovingAverage

/**
  *
  */
class AccumulationDistributionOscillator(pname: String,  pfastPeriod: Int, pslowPeriod: Int) extends TradeBarIndicator(pname: String){
  private val _period: Int = math.max(pfastPeriod, pslowPeriod)
  private val  _ad: AccumulationDistribution = new AccumulationDistribution(name + "_AD")
  private val _emaFast: ExponentialMovingAverage = new ExponentialMovingAverage(name + "_Fast", pfastPeriod)
  private val _emaSlow: ExponentialMovingAverage = new ExponentialMovingAverage(name + "_Slow", pslowPeriod);

  def this(pfastPeriod: Int, pslowPeriod: Int) {
    this("ADOSC(%d, %d)".format(pfastPeriod, pslowPeriod), pfastPeriod, pslowPeriod)
  }

  override def isReady: Boolean = samples >= _period

  override def reset = {
    _ad.reset
    _emaFast.reset
    _emaSlow.reset
    super.reset
  }

  override def computeNextValue(input: TradeBar): Double = {
    _ad.update(input)
    _emaFast.update(_ad.current)
    _emaSlow.update(_ad.current)

    if(isReady) _emaFast.current.value - _emaSlow.current.value else 0.0
  }
}
