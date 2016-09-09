/**
  *
  */
package quanter.indicators

import quanter.indicators.MovingAverageType._
import quanter.indicators.MovingAverageType.MovingAverageType
import quanter.indicators.IndicatorExtensions._

/**
  *
  */
// TODO: 这个类的主构造函数为什么不能有默认参数
class RelativeStrengthIndex(pname: String, pperiod: Int, pmovingAverageType: MovingAverageType) extends Indicator(pname) {

  private var _previousInput: IndicatorDataPoint = null
  private var _movingAverageType: MovingAverageType = movingAverageType
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
    if(_previousInput != null && input.value >= _previousInput.value) {
      averageGain.update(input.time, input.value - _previousInput.value)
      averageLoss.update(input.time, 0)
    } else if(_previousInput != null && input.value < _previousInput.value) {
      averageGain.update(input.time, 0)
      averageLoss.update(input.time, _previousInput.value - input.value)
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
}
