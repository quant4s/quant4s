/**
  *
  */
package org.quant4s.indicators

import org.quant4s.QuanterUnitSpec
import org.quant4s.data.market.TradeBar

/**
  *
  */
class AverageDirectionalIndexSpec extends CommonIndicatorSpec[TradeBar]{
  epsilon = 1.0

  override protected def createIndicator: IndicatorBase[TradeBar] = new AverageDirectionalIndex("adx", 14)

  override protected def testColumnName: String = "ADX 14"

  override protected def testFileName: String = "spy_with_adx.txt"
}
