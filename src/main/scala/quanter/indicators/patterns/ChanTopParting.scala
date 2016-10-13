/**
  *
  */
package quanter.indicators.patterns

import quanter.data.market.TradeBar
import quanter.indicators.window.ReadOnlyWindow

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
