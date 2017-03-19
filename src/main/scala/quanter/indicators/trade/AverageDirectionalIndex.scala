package quanter.indicators.trade

import org.quant4s.data.market.TradeBar
import quanter.indicators.{FunctionalIndicator, IndicatorBase, IndicatorDataPoint, TradeBarIndicator}

/**
  *
  */
class AverageDirectionalIndex(pname: String, pperiod: Int) extends TradeBarIndicator(pname: String) {
  private val _period: Int = pperiod
  private var _previousInput: TradeBar = null
  private var _trueRange: IndicatorBase[TradeBar] = new FunctionalIndicator[TradeBar](pname + "_TrueRange",
    currentBar => _computeTrueRange(currentBar),
    isReady => _previousInput != null)
  private var _directionalMovementPlus: IndicatorBase[TradeBar] = new FunctionalIndicator[TradeBar](name + "_PositiveDirectionalMovement", currentBar => _computePositiveDirectionalMovement(currentBar), isReady => _previousInput != null)
  private var _directionalMovementMinus: IndicatorBase[TradeBar] = new FunctionalIndicator[TradeBar](name + "_NegativeDirectionalMovement",
    currentBar =>_computeNegativeDirectionalMovement(currentBar),
    isReady => _previousInput != null)
  private var _smoothedTrueRange: IndicatorBase[IndicatorDataPoint] = new FunctionalIndicator[IndicatorDataPoint](name + "_SmoothedTrueRange",
    currentBar => _computeSmoothedTrueRange(_period),
    isReady => _previousInput != null);
  private var _smoothedDirectionalMovementMinus: IndicatorBase[IndicatorDataPoint] =  new FunctionalIndicator[IndicatorDataPoint](name + "_SmoothedDirectionalMovementPlus",
    currentBar => _computeSmoothedDirectionalMovementPlus(_period),
    isReady => _previousInput != null);
  private var _smoothedDirectionalMovementPlus: IndicatorBase[IndicatorDataPoint] = new FunctionalIndicator[IndicatorDataPoint](name + "_SmoothedDirectionalMovementMinus",
    currentBar => _computeSmoothedDirectionalMovementMinus(_period),
    isReady => _previousInput != null);


  var positiveDirectionalIndex: IndicatorBase[IndicatorDataPoint] = new FunctionalIndicator[IndicatorDataPoint](name + "_PositiveDirectionalIndex",
    currentBar => _computePositiveDirectionalIndex(),
    isReady => _directionalMovementPlus.isReady && _trueRange.isReady,
    ()=>{
      _directionalMovementPlus.reset
      _trueRange.reset
    })

  var negativeDirectionalIndex: IndicatorBase[IndicatorDataPoint] =  new FunctionalIndicator[IndicatorDataPoint](name + "_NegativeDirectionalIndex",
    input => _computeNegativeDirectionalIndex(),
    negativeDirectionalIndex => _directionalMovementMinus.isReady && _trueRange.isReady,
    ()=>{
      _directionalMovementMinus.reset
      _trueRange.reset
    })

  private def _computeTrueRange(input: TradeBar): Double = {
    if (_previousInput == null)  0.0;
    else
      (math.max(math.abs(input.low - _previousInput.close), math.max(_trueRange.current.value, math.abs(input.high - _previousInput.close))))
  }

  private def _computePositiveDirectionalMovement(input: TradeBar): Double = {
    if (_previousInput == null)  0.0;
    else if ((input.high - _previousInput.high) >= (_previousInput.low - input.low) && (input.high - _previousInput.high) > 0)
         input.high - _previousInput.high
    else 0.0
  }

  private def _computeNegativeDirectionalMovement(input: TradeBar): Double =
  {
    if (_previousInput == null) 0.0
    else if((_previousInput.low - input.low) > (input.high - _previousInput.high) && (_previousInput.low - input.low) > 0)
      _previousInput.low - input.low
    else 0.0
  }
  private def _computePositiveDirectionalIndex(): Double = {
    if (_smoothedTrueRange == 0) 0.0
    else (_smoothedDirectionalMovementPlus.current.value / _smoothedTrueRange.current.value) * 100
  }

  private def _computeNegativeDirectionalIndex(): Double = {
    if (_smoothedTrueRange == 0) return 0.0
    else (_smoothedDirectionalMovementMinus.current.value / _smoothedTrueRange.current.value) * 100
  }

  private def _computeSmoothedTrueRange(period: Int): Double = {
    if (samples < period)_smoothedTrueRange.current.value + _trueRange.current.value
    else _smoothedTrueRange.current.value - (_smoothedTrueRange.current.value / period) + _trueRange.current.value
  }

  private def _computeSmoothedDirectionalMovementPlus(period: Int) = {
    if (samples < period) _smoothedDirectionalMovementPlus.current.value + _directionalMovementPlus.current.value
    else _smoothedDirectionalMovementPlus.current.value - (_smoothedDirectionalMovementPlus.current.value / period) + _directionalMovementPlus.current.value
  }

  /// <summary>
  /// Computes the Smoothed Directional Movement Minus value.
  /// </summary>
  /// <param name="period">The period.</param>
  /// <returns></returns>
  private def _computeSmoothedDirectionalMovementMinus(period: Int) = {
    if (samples < period) _smoothedDirectionalMovementMinus.current.value + _directionalMovementMinus.current.value
    else _smoothedDirectionalMovementMinus.current.value - (_smoothedDirectionalMovementMinus.current.value / 14) + _directionalMovementMinus.current.value
  }


  override def isReady: Boolean = samples >= _period

  override def computeNextValue(input: TradeBar): Double = {
    _trueRange.update(input);
    _directionalMovementPlus.update(input);
    _directionalMovementMinus.update(input);
    _smoothedTrueRange.update(current);
    _smoothedDirectionalMovementMinus.update(current);
    _smoothedDirectionalMovementPlus.update(current);
    if (_previousInput != null)
    {
      positiveDirectionalIndex.update(current);
      negativeDirectionalIndex.update(current);
    }
    val diff = math.abs(positiveDirectionalIndex - negativeDirectionalIndex);
    val sum = positiveDirectionalIndex + negativeDirectionalIndex;
    val value = if(sum == 0)  50 else ((_period - 1) * current.value + 100 * diff / sum ) / _period;
    _previousInput = input;
    return value;
  }

  override def reset = {
    super.reset
    _previousInput = null;
    _trueRange.reset
    _directionalMovementPlus.reset
    _directionalMovementMinus.reset
    _smoothedTrueRange.reset
    _smoothedDirectionalMovementMinus.reset
    _smoothedDirectionalMovementPlus.reset
    positiveDirectionalIndex.reset
    negativeDirectionalIndex.reset

  }
}
