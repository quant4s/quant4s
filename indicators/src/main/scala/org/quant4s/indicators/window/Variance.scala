package org.quant4s.indicators.window

import org.quant4s.indicators.IndicatorDataPoint

/**
  *
  */
class Variance(pname: String, pperiod: Int) extends WindowIndicator[IndicatorDataPoint](pname, pperiod){
  var _rollingSum: Double = 0.0
  var _rollingSumOfSquares: Double = 0.0

  def this(pperiod: Int) {
    this("VAR" + pperiod, pperiod)
  }

  override def isReady: Boolean = samples >= period
  override def reset: Unit =  {
    _rollingSum = 0.0;
    _rollingSumOfSquares = 0.0;
    super.reset;
  }

  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = {
    _rollingSum += input.value;
    _rollingSumOfSquares += input.value * input.value;

    if (samples < period)
       0
    else {
      val meanValue1 = _rollingSum / period;
      val meanValue2 = _rollingSumOfSquares / period;

      val removedValue = window.get(period - 1)
      _rollingSum -= removedValue.value
      _rollingSumOfSquares -= removedValue.value * removedValue.value

      meanValue2 - meanValue1 * meanValue1
    }

  }
}
