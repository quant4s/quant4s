/**
  *
  */
package org.quant4s.indicators

import java.util.Date

import org.quant4s.QuanterUnitSpec
import org.quant4s.data.market.TradeBar

/**
  *
  */
class AverageTrueRangeSpec extends QuanterUnitSpec {
  describe ("用外部文件测试 ATR") {
    it("spy_atr.txt ATR14 列") {
      val atr = new AverageTrueRange(14, MovingAverageType.Simple)
      TestHelper.testTradeBarIndicator(atr, "spy_atr.txt", "ATR14")
    }
  }

  describe("测试ATR 指标 属性") {
    it("Reset 后 ATR 应该为默认状态") {
      val atr = new AverageTrueRange(14, MovingAverageType.Simple)

      val bar = new TradeBar()
      bar.time = new Date()
      bar.open = 1.0
      bar.high = 3.0
      bar.low = 0.5
      bar.close = 2.75
      bar.volume = 1234567890
      atr.update(bar)

      atr.reset

      TestHelper.assertIndicatorIsInDefaultState(atr)
      TestHelper.assertIndicatorIsInDefaultState(atr.trueRange)
    }
    it("TrueRangePropertyIsReadyAfterOneSample") {
      val atr = new AverageTrueRange(14, MovingAverageType.Simple)

      atr.trueRange.isReady should be(false)

      val bar = new TradeBar()
      bar.time = new Date()
      bar.open = 1.0
      bar.high = 3.0
      bar.low = 0.5
      bar.close = 2.75
      bar.volume = 1234567890
      atr.update(bar)

      atr.trueRange.isReady should be(true)

    }
  }
}
