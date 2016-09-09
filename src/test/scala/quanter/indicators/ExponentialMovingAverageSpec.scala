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
  describe ("用外部文件测试") {
    it("spy_with_indicators.txt EMA14 列") {
      val ema = new ExponentialMovingAverage(14)
      TestHelper.testIndicator(ema, "EMA14", 0.025)
    }
  }
}
