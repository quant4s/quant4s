/**
  *
  */
package org.quant4s.indicators

import org.quant4s.data.market.TradeBar

/**
  *
  */
class OnBalanceVolumeSpec extends CommonIndicatorSpec[TradeBar]{
  override protected def createIndicator: IndicatorBase[TradeBar] = new OnBalanceVolume("OBV")

  override protected def testColumnName: String = "OBV"

  override protected def testFileName: String = "spy_obv.txt"
}
