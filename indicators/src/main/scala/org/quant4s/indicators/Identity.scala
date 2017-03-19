/**
  *
  */
package org.quant4s.indicators

/**
  *
  */
class Identity(name: String) extends Indicator(name){
  override def isReady: Boolean = samples > 0

  override def computeNextValue(input: IndicatorDataPoint): Double = input.value
}
