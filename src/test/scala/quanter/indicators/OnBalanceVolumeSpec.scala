/**
  *
  */
package quanter.indicators

import quanter.data.market.TradeBar

/**
  *
  */
class OnBalanceVolumeSpec extends CommonIndicatorSpec[TradeBar]{
  override protected def createIndicator: IndicatorBase[TradeBar] = new OnBalanceVolume("OBV")

  override protected def testColumnName: String = "OBV"

  override protected def testFileName: String = "spy_obv.txt"
}
