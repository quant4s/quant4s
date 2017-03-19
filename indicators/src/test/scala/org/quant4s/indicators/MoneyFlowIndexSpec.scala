/**
  *
  */
package org.quant4s.indicators

import org.quant4s.QuanterUnitSpec
import org.quant4s.data.market.TradeBar

/**
  *
  */
class MoneyFlowIndexSpec extends CommonIndicatorSpec[TradeBar] {

  override protected def createIndicator: IndicatorBase[TradeBar] = new MoneyFlowIndex(20)

  override protected def testColumnName: String = "Money Flow Index 20"

  override protected def testFileName: String = "spy_mfi.txt"

  describe("测试MFI 指标计算") {
    it("没有交易量的测试"){
      val mfi = new MoneyFlowIndex(3)
      for(data <- TestHelper.getDataStream(4))
      {
        val tradeBar = new TradeBar()
        {
          open = data.value
          close = data.value
          high = data.value
          low = data.value
          volume = 0
        }
        mfi.update(tradeBar)
      }

      mfi.current.value should be(100.0)
    }
  }
}
