/**
  *
  */
package quanter.indicators
import quanter.data.market.TradeBar
import quanter.indicators.window.{Maximum, Minimum, Sum}
import quanter.indicators.IndicatorExtensions._

/**
  *
  */
class Stochastic(pname: String, pperiod: Int, pkPeriod: Int, pdPeriod: Int) extends  TradeBarIndicator(pname) {

  private val _maximum: IndicatorBase[IndicatorDataPoint] = new Maximum(name + "_Max", pperiod)
  private val _mininum: IndicatorBase[IndicatorDataPoint] = new Minimum(name + "_Min", pperiod)
  private val _sumFastK: IndicatorBase[IndicatorDataPoint] = new Sum(name + "_SumFastK", pkPeriod)
  private val _sumSlowK: IndicatorBase[IndicatorDataPoint] = new Sum(name + "_SumD", pdPeriod)

  val fastStoch = new FunctionalIndicator[TradeBar](name + "_FastStoch",
    input => computeFastStoch(pperiod, input),
    fastStoch => _maximum.isReady,
    () => _maximum.reset
  )

  val stochK = new FunctionalIndicator[TradeBar](name + "_StochK",
    input => computeStochK(pperiod, pkPeriod, input),
    stochK => _maximum.isReady,
    () => _maximum.reset)

  val stochD = new FunctionalIndicator[TradeBar](name + "_StochD",
    input => computeStochD(pperiod, pkPeriod, pdPeriod),
    stochD => _maximum.isReady,
    () => _maximum.reset
  )

  override def isReady: Boolean = fastStoch.isReady && stochK.isReady && stochD.isReady

  override def reset: Unit = {
    fastStoch.reset
    stochK.reset
    stochD.reset
    _sumFastK.reset
    _sumSlowK.reset
    super.reset
  }

  override def computeNextValue(input: TradeBar): Double = {
    _maximum.update(input.time, input.high)
    _mininum.update(input.time, input.low)
    fastStoch.update(input)
    stochK.update(input)
    stochD.update(input)
    log.debug("Stochastic's value is %f".format( fastStoch.current.value))
    fastStoch.current.value
  }

  def computeFastStoch(period: Int, input: TradeBar) = {
    val denominator = _maximum - _mininum
    val numerator = input.close - _mininum.current.value
    val fastStoch = {
      if (denominator == 0) 0
      else {
        if (_maximum.samples >= period) numerator / denominator
        else 0
      }
    }
    _sumFastK.update(input.time, fastStoch)
    fastStoch * 100
  }


  def computeStochK(period: Int, constantK: Int, input: TradeBar) = {
    val stochK: Double = if (_maximum.samples >= (period + constantK - 1)) _sumFastK / constantK else 0.0
    _sumSlowK.update(input.time, stochK)
    stochK * 100
  }

  def computeStochD(period: Int, constantK: Int, constantD: Int) = {
    val stochD: Double = if (_maximum.samples >= (period + constantK + constantD - 2)) _sumSlowK / constantD else 0.0
    stochD * 100
  }
}
