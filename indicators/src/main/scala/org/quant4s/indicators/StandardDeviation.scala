package org.quant4s.indicators

import org.quant4s.indicators.window.{ReadOnlyWindow, Variance}

/**
  *
  */
class StandardDeviation(pname: String, pperiod: Int) extends Variance(pname, pperiod) {
  def this(pperiod: Int) {
    this("STD" + pperiod, pperiod)
  }

  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = {
    math.sqrt(super.computeNextValue(window, input))
  }

}
