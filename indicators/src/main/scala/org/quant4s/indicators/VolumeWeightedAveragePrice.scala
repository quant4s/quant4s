/**
  *
  */
package org.quant4s.indicators

import org.quant4s.data.market.TradeBar
import org.quant4s.indicators.composite.CompositeIndicator

/**
  *
  */
class VolumeWeightedAveragePrice(pname: String, pperiod: Int) extends TradeBarIndicator(pname){
  val _price: Identity = new Identity("Price")
  val _volume: Identity = new Identity("Volume")
//  val _vwap: CompositeIndicator[IndicatorDataPoint] =  _price.weightedBy(_volume, pperiod);
  override def isReady: Boolean = ???

  override def computeNextValue(input: TradeBar): Double = ???
}
