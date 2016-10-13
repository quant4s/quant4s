/**
  *
  */
package quanter.indicators.window

import quanter.indicators.IndicatorDataPoint

/**
  *
  */
class WindowIdentity(pname: String, pperiod: Int) extends WindowIndicator[IndicatorDataPoint](pname, pperiod){

  def this(period: Int) {
    this("WIN_ID_" + period, period)
  }

  override def isReady: Boolean = samples >= period

  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = input.value
}
