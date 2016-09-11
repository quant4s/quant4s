package quanter.indicators.trade

import quanter.QuanterUnitSpec
import quanter.data.market.TradeBar
import quanter.indicators.{IndicatorBase, CommonIndicatorSpec}

/**
  * Created by joe on 16-4-2.
  */
class PartingIndicatorSpec extends CommonIndicatorSpec[TradeBar] {
  override protected def createIndicator(): IndicatorBase[TradeBar] = new ChanParting()
  override protected def testFileName: String = "spy_tradebar_parting.csv"
  override protected def testColumnName: String = "parting"
}
