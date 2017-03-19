/**
  *
  */
package org.quant4s.indicators

import org.quant4s.QuanterUnitSpec
import org.quant4s.data.market.TradeBar

/**
  *
  */

  class CommodityChannelIndexSpec extends CommonIndicatorSpec[TradeBar] {
  epsilon = 0.01
  override protected def createIndicator: IndicatorBase[TradeBar] = new CommodityChannelIndex(14)

  override protected def testColumnName: String = "Commodity Channel Index (CCI) 14"

  override protected def testFileName: String = "spy_cci.txt"
}
