/**
  *
  */
package org.quant4s.indicators.patterns

import org.quant4s.data.market.TradeBar
import org.quant4s.indicators.window.ReadOnlyWindow

import scala.collection.mutable.ArrayBuffer

/**
  *
  */
class ChanTopParting(pname: String, pperiod: Int) extends CandlestickPattern(pname, pperiod) {
  private var _bars = ArrayBuffer[TradeBar]()

  override protected def computeNextValue(window: ReadOnlyWindow[TradeBar], input: TradeBar): Double = {
    0.0
  }
}
