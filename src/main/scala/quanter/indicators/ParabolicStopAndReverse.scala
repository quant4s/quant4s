/**
  *
  */
package quanter.indicators
import quanter.data.market.TradeBar

/**
  *
  */
class ParabolicStopAndReverse(ppname: String, afStart:Double, afIncrement: Double , afMax: Double) extends TradeBarIndicator(ppname){
  def this(afStart:Double = 0.02, afIncrement: Double = 0.02, afMax: Double = 0.2) {
    this("PSAR(%f,%f,%f)".format(afStart, afIncrement, afMax), afStart, afIncrement, afMax)
  }

  private var _isLong: Boolean = false
  private var _previousBar: TradeBar = null
  private var _sar: Double = 0
  private var _ep: Double = 0
  private var _outputSar: Double = 0
  private var _af: Double = afStart
  private val _afInit = afStart
  private val _afMax = afMax
  private val _afIncrement = afIncrement

  override def isReady: Boolean = samples >= 2

  override def reset: Unit = {
    _af = _afInit
    super.reset
  }

  override def computeNextValue(input: TradeBar): Double = {
    var nextValue: Double = 0
    if (samples == 1) {
      _previousBar = input

      // return a value that's close to where we will be, returning 0 doesn't make sense
      nextValue = input.close
    } else if(samples == 2) {
      _init(input)
      _previousBar = input
      nextValue = _sar
    } else {
      if(_isLong) _handleLongPosition(input)
      else _handleShortPosition(input)

      _previousBar = input
      nextValue = _outputSar
    }

    nextValue
  }

  def _init(currentBar: TradeBar): Unit = {
    _isLong = currentBar.close >= _previousBar.close

    // init sar and Extreme price
    if (_isLong) {
      _ep = math.min(currentBar.high, _previousBar.high)
      _sar = _previousBar.low
    }
    else {
      _ep = math.min(currentBar.low, _previousBar.low)
      _sar = _previousBar.high
    }
  }

  def _handleLongPosition(currentBar: TradeBar): Unit = {
    // Switch to short if the low penetrates the SAR value.
    if (currentBar.low <= _sar)
    {
      // Switch and Overide the SAR with the ep
      _isLong = false
      _sar = _ep

      // Make sure the overide SAR is within yesterday's and today's range.
      if (_sar < _previousBar.high)
        _sar = _previousBar.high
      if (_sar < currentBar.high)
        _sar = currentBar.high

      // Output the overide SAR 
      _outputSar = _sar

      // Adjust af and ep
      _af = _afInit
      _ep = currentBar.low

      // Calculate the new SAR
      _sar = _sar + _af * (_ep - _sar)

      // Make sure the new SAR is within yesterday's and today's range.
      if (_sar < _previousBar.high)
        _sar = _previousBar.high
      if (_sar < currentBar.high)
        _sar = currentBar.high

    }

    // No switch
    else {
      // Output the SAR (was calculated in the previous iteration) 
      _outputSar = _sar

      // Adjust af and ep.
      if (currentBar.high > _ep)
      {
        _ep = currentBar.high
        _af += _afIncrement
        if (_af > _afMax)
          _af = _afMax
      }

      // Calculate the new SAR
      _sar = _sar + _af * (_ep - _sar)

      // Make sure the new SAR is within yesterday's and today's range.
      if (_sar > _previousBar.low)
        _sar = _previousBar.low
      if (_sar > currentBar.low)
        _sar = currentBar.low
    }
  }

  def _handleShortPosition(currentBar: TradeBar): Unit = {
    // Switch to long if the high penetrates the SAR value.
    if (currentBar.high >= _sar)
    {
      // Switch and Overide the SAR with the ep
      _isLong = true
      _sar = _ep

      // Make sure the overide SAR is within yesterday's and today's range.
      if (_sar > _previousBar.low)
        _sar = _previousBar.low
      if (_sar > currentBar.low)
        _sar = currentBar.low

      // Output the overide SAR 
      _outputSar = _sar

      // Adjust af and ep
      _af = _afInit
      _ep = currentBar.high

      // Calculate the new SAR
      _sar = _sar + _af * (_ep - _sar)

      // Make sure the new SAR is within yesterday's and today's range.
      if (_sar > _previousBar.low)
        _sar = _previousBar.low
      if (_sar > currentBar.low)
        _sar = currentBar.low
    }
    //No switch
    else {
      // Output the SAR (was calculated in the previous iteration)
      _outputSar = _sar

      // Adjust af and ep.
      if (currentBar.low < _ep)
      {
        _ep = currentBar.low
        _af += _afIncrement
        if (_af > _afMax)
          _af = _afMax
      }

      // Calculate the new SAR
      _sar = _sar + _af * (_ep - _sar)

      // Make sure the new SAR is within yesterday's and today's range.
      if (_sar < _previousBar.high)
        _sar = _previousBar.high
      if (_sar < currentBar.high)
        _sar = currentBar.high
    }
  }

}
