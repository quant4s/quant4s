/**
  *
  */
package quanter.indicators

import quanter.data.market.TradeBar

/**
  *
  */
class AroonOscillatorSpec extends CommonIndicatorSpec[TradeBar] {
  epsilon = 0.001
  override protected def createIndicator: IndicatorBase[TradeBar] = new AroonOscillator(14, 14)

  override protected def testColumnName: String = "Aroon Oscillator 14"

  override protected def testFileName: String = "spy_aroon_oscillator.txt"
}
