/**
  *
  */
package quanter.indicators

import quanter.data.market.TradeBar
import quanter.indicators.IndicatorExtensions._

/**
  *
  */
class AverageDirectionalIndex(pname: String, pperiod: Int) extends TradeBarIndicator(pname) {
  var _previousInput: TradeBar = null
  val _period = pperiod

  val trueRange = new FunctionalIndicator[TradeBar](pname + "_TrueRange",
    currentBar => computeTrueRange(currentBar),
    isReady => _previousInput != null
    )
  val directionalMovementPlus = new FunctionalIndicator[TradeBar](pname + "_PositiveDirectionalMovement",
    currentBar => computePositiveDirectionalMovement(currentBar),
    isReady => _previousInput != null
    )
  val directionalMovementMinus = new FunctionalIndicator[TradeBar](pname + "_NegativeDirectionalMovement",
    currentBar => computeNegativeDirectionalMovement(currentBar),
    isReady => _previousInput != null
    )
  val smoothedDirectionalMovementPlus = new FunctionalIndicator[IndicatorDataPoint](pname + "_SmoothedDirectionalMovementPlus",
    currentBar => computeSmoothedDirectionalMovementPlus(_period),
    isReady => _previousInput != null
    )
  val smoothedDirectionalMovementMinus = new FunctionalIndicator[IndicatorDataPoint](pname + "_SmoothedDirectionalMovementMinus",
    currentBar => computeSmoothedDirectionalMovementMinus(_period),
    isReady => _previousInput != null
    )
  val smoothedTrueRange = new FunctionalIndicator[IndicatorDataPoint](pname + "_SmoothedTrueRange",
    currentBar => computeSmoothedTrueRange(_period),
    isReady => _previousInput != null
    )

  val positiveDirectionalIndex =  new FunctionalIndicator[IndicatorDataPoint](pname + "_PositiveDirectionalIndex",
    input => computePositiveDirectionalIndex(),
    positiveDirectionalIndex => directionalMovementPlus.isReady && trueRange.isReady,
    () => {
      directionalMovementPlus.reset
      trueRange.reset
    }
  )
  val negativeDirectionalIndex = new FunctionalIndicator[IndicatorDataPoint](pname + "_NegativeDirectionalIndex",
    input => computeNegativeDirectionalIndex(),
    negativeDirectionalIndex => directionalMovementMinus.isReady && trueRange.isReady,
    () => {
      directionalMovementMinus.reset
      trueRange.reset
    }
    )
  override def isReady: Boolean = samples >= _period

  override def reset = ???

  override def computeNextValue(input: TradeBar): Double = {

    trueRange.update(input)
    directionalMovementPlus.update(input)
    directionalMovementMinus.update(input)
    smoothedTrueRange.update(current)
    smoothedDirectionalMovementMinus.update(current)
    smoothedDirectionalMovementPlus.update(current)
    if (_previousInput != null) {
      positiveDirectionalIndex.update(current)
      negativeDirectionalIndex.update(current)
    }

    _previousInput = input

    val diff = math.abs(positiveDirectionalIndex - negativeDirectionalIndex)
    val sum = positiveDirectionalIndex + negativeDirectionalIndex
    if(sum == 0)  50
    else (current * (_period - 1) + 100 * diff / sum ) / _period
  }


  def computeSmoothedDirectionalMovementPlus(period: Int): Double = {
    if(!isReady) smoothedDirectionalMovementPlus.current + directionalMovementPlus.current
    else directionalMovementPlus.current + smoothedDirectionalMovementPlus.current - (smoothedDirectionalMovementMinus.current / period)
  }

  def computeSmoothedDirectionalMovementMinus(period: Int): Double = {
    if(!isReady) smoothedDirectionalMovementMinus.current + directionalMovementMinus.current
    else  directionalMovementMinus.current + smoothedDirectionalMovementMinus.current - (smoothedDirectionalMovementMinus.current / period)
  }

  def computeSmoothedTrueRange(period: Int): Double = {
    if(!isReady) smoothedTrueRange.current + trueRange.current
    else trueRange.current + smoothedTrueRange.current - (smoothedTrueRange.current / period)
  }

  def computeTrueRange(input: TradeBar): Double = {
    if(_previousInput == null) 0.0
    else math.max(math.abs(input.low - _previousInput.close), math.max(trueRange.current.value, math.abs(input.high - _previousInput.close)))
  }

  def computePositiveDirectionalMovement(input: TradeBar): Double = {
    if (_previousInput == null) 0.0
    else if((input.high - _previousInput.high) >= (_previousInput.low - input.low) &&((input.high - _previousInput.high) > 0)) input.high - _previousInput.high
    else 0.0
  }

  def computeNegativeDirectionalMovement(input: TradeBar): Double = {
    if (_previousInput == null) return 0.0
    else if(((_previousInput.low - input.low) > (input.high - _previousInput.high)) && ((_previousInput.low - input.low) > 0)) _previousInput.low - input.low
    else 0.0
  }

  def computePositiveDirectionalIndex(): Double = {
    if (smoothedTrueRange.current.value == 0) 0.0
    else (smoothedDirectionalMovementPlus / smoothedTrueRange) * 100
  }

  def computeNegativeDirectionalIndex(): Double = {
    if (smoothedTrueRange.current.value == 0) 0.0
    else (smoothedDirectionalMovementMinus / smoothedTrueRange) * 100
  }
}
