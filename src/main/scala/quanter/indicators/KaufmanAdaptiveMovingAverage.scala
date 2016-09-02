/**
  *
  */
package quanter.indicators

import quanter.indicators.window.{ReadOnlyWindow, WindowIndicator}

/**
  *
  */
class KaufmanAdaptiveMovingAverage(pname: String, pperiod: Int) extends WindowIndicator[IndicatorDataPoint](pname, pperiod + 1){
  def this (pperiod: Int) {
    this("KAMA_" + pperiod, pperiod)
  }

  private final val ConstMax: Double = 2 / (30 + 1)
  private final val ConstDiff = 2 / (2 + 1) - ConstMax

  private var _sumRoc1: Double = 0.0
  private var _periodRoc: Double = 0.0
  private var _prevKama: Double = 0.0
  private var _trailingValue: Double = 0.0

  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = {
    var nextValue: Double = 0.0
    if(samples < period && samples > 1) {
      _sumRoc1 += math.abs(input.value - window.get(1).value)
      nextValue = input.value
    } else {
      if (samples == period) {
        _sumRoc1 += math.abs(input.value - window.get(1).value)
        _prevKama = window.get(1).value
      }

      var newTrailingValue = window.get(period - 1)
      _periodRoc = input.value - newTrailingValue.value

      if(samples > period) {
        // Adjust sumROC1:
        // - Remove trailing ROC1
        // - Add new ROC1
        _sumRoc1 -= math.abs((_trailingValue - newTrailingValue.value))
        _sumRoc1 += math.abs(input.value - window.get(1).value)
      }

      _trailingValue = newTrailingValue.value
      var efficiencyRatio: Double = if (_sumRoc1 <= _periodRoc || _sumRoc1 == 0) 1 else math.abs(_periodRoc / _sumRoc1)
      // Calculate the smoothing constant
      var smoothingConstant = efficiencyRatio * ConstDiff + ConstMax
      smoothingConstant *= smoothingConstant
      // Calculate the KAMA like an EMA, using the smoothing constant as the adaptive factor.
      _prevKama = (input.value - _prevKama) * smoothingConstant + _prevKama
      nextValue = _prevKama
    }

    nextValue
  }

  override def isReady: Boolean =  {
    samples >= period
  }

  override def reset: Unit = {
    _sumRoc1 = 0
    _periodRoc = 0
    _prevKama = 0
    _trailingValue = 0
    super.reset
  }
}
