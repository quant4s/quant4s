/**
  *
  */
package quanter.indicators

import quanter.QuanterUnitSpec
import quanter.indicators.window.ExponentialMovingAverage

/**
  *
  */
class ExponentialMovingAverageSpec extends QuanterUnitSpec {
  override val epsilon = 0.01
  describe ("用外部文件测试 EMA") {
    it("spy_with_indicators.txt EMA14 列") {
      val ema = new ExponentialMovingAverage(14)
      TestHelper.testIndicator(ema, "EMA14", epsilon)
    }
  }

  describe("测试EMA 指标 Reset") {
    it("EMA 应该为默认状态") {
      val ema = new ExponentialMovingAverage(3)

      for(data <- TestHelper.getDataStream(5, null))
      {
        ema.update(data)
      }
      ema.isReady should be(true)
      ema.current.value should not be(0)
      ema.samples should not be(0)

      ema.reset

      TestHelper.assertIndicatorIsInDefaultState(ema)
    }
  }
}
