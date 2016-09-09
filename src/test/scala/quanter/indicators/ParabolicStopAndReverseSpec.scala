/**
  *
  */
package quanter.indicators

import quanter.data.market.TradeBar

/**
  *
  */
class ParabolicStopAndReverseSpec extends CommonIndicatorSpec[TradeBar]{
  override protected def createIndicator(): IndicatorBase[TradeBar] = new ParabolicStopAndReverse()
  override protected def testFileName: String = "datas/spy_parabolic_SAR.txt"
  override protected def testColumnName: String = "Parabolic SAR 0.02 0.20"

}
