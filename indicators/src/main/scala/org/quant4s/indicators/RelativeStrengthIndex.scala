/**
  *
  */
package org.quant4s.indicators

import org.quant4s.indicators.MovingAverageType._
import org.quant4s.indicators.MovingAverageType.MovingAverageType
import org.quant4s.indicators.IndicatorExtensions._

/**
  *
  */
class RelativeStrengthIndex(pname: String, pperiod: Int, pmovingAverageType: MovingAverageType) extends Indicator(pname) {

  private var _previousInput: IndicatorDataPoint = null
  private var _movingAverageType: MovingAverageType = pmovingAverageType
  private var _averageLoss: IndicatorBase[IndicatorDataPoint] = movingAverageType.asIndicator(name + "Down", pperiod)
  private var _averageGain: IndicatorBase[IndicatorDataPoint] = movingAverageType.asIndicator(name + "Up", pperiod)

  def this(pperiod: Int, pmovingAverageType: MovingAverageType.MovingAverageType = Wilders) {
    this("RSI%d" format pperiod, pperiod, pmovingAverageType)
  }

  def this(pname: String, pperiod: Int) {
    this(pname, pperiod, MovingAverageType.Wilders)
  }

  def movingAverageType = _movingAverageType
  def movingAverageType_=(newValue: MovingAverageType): Unit = {
    _movingAverageType = newValue
  }

  def averageLoss = _averageLoss
  def averageLoss_=(newValue: IndicatorBase[IndicatorDataPoint]): Unit = {
    _averageLoss = newValue
  }

  def averageGain = _averageGain
  def averageGain_=(newValue: IndicatorBase[IndicatorDataPoint]): Unit = {
    _averageGain = newValue
  }

  override def isReady: Boolean = averageGain.isReady && averageLoss.isReady

  override def computeNextValue(input: IndicatorDataPoint): Double = {
    var nextValue: Double = 0.0

    if(_previousInput != null) {
      averageGain.update(input.time, math.max(input.value - _previousInput.value, 0))
      averageLoss.update(input.time, math.abs(input.value - _previousInput.value))
    }

    _previousInput = input
    if(averageLoss == 0) nextValue = 100
    else {
      val rs = averageGain / averageLoss
      nextValue = 100-(100/(1+rs))
    }

    nextValue
  }

  override def reset: Unit = {
    averageGain.reset
    averageLoss.reset
    super.reset
  }

  override def toJson: String = "{\"symbol\":%s,\"rsi\":%f, \"time\":%d}"
    .format(symbol, current.value, time.getTime())

}
