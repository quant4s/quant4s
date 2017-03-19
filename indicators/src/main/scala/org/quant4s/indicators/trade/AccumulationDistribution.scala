package org.quant4s.indicators.trade

import org.quant4s.data.market.TradeBar
import org.quant4s.indicators.TradeBarIndicator

/**
  * AD = AD + ((Close - Low) - (High - Close)) / (High - Low) * Volume
  */
class AccumulationDistribution(pname: String) extends TradeBarIndicator(pname: String) {
  override def isReady: Boolean = samples > 0

  override def computeNextValue(input: TradeBar): Double =  {
    var range = input.high - input.low;
    current.value + (if(range > 0) ((input.close - input.low) - (input.high - input.close)) / range * input.volume else 0.0)
  }
}
