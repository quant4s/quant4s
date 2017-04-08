package org.quant4s.indicators.trade

import org.quant4s.data.market.TradeBar
import org.quant4s.indicators.CommonIndicatorSpec
import org.quant4s.QuanterUnitSpec
import org.quant4s.indicators.{CommonIndicatorSpec, IndicatorBase}

/**
  * Created by joe on 16-4-2.
  */
class PartingIndicatorSpec extends CommonIndicatorSpec[TradeBar] {
  override protected def createIndicator(): IndicatorBase[TradeBar] = new ChanParting()
  override protected def testFileName: String = "spy_tradebar_parting.csv"
  override protected def testColumnName: String = "parting"
}
