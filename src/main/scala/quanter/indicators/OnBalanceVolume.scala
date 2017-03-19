/**
  *
  */
package quanter.indicators

import org.quant4s.data.market.TradeBar

/**
  *
  */
class OnBalanceVolume(ppname: String) extends TradeBarIndicator(ppname) {

  var _previousInput: TradeBar = null

  override def isReady: Boolean = _previousInput != null

  override def reset: Unit = {
    _previousInput = null
    super.reset
  }

  override def toJson: String = "{\"symbol\":%s,\"obv\":%f, \"time\":%d}"
    .format(symbol, current.value, time.getTime())

  override def computeNextValue(input: TradeBar): Double = {
    var obv = current.value

    if (_previousInput != null)
    {
      if (input.value > _previousInput.value)
      {
        obv += input.volume
        update(input)
      }
      else if (input.value < _previousInput.value)
      {
        obv -= input.volume
        update(input)
      }
    }
    else
    {
      obv = input.volume
      update(input)
    }

    _previousInput = input

    obv
  }
}
