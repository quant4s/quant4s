/**
  *
  */
package org.quant4s.indicators

import org.quant4s.data.market.TradeBar

/**
  *
  */
class ParabolicStopAndReverseSpec extends CommonIndicatorSpec[TradeBar]{
  override protected def createIndicator(): IndicatorBase[TradeBar] = new ParabolicStopAndReverse()
  override protected def testFileName: String = "spy_parabolic_SAR.txt"
  override protected def testColumnName: String = "Parabolic SAR 0.02 0.20"

}
