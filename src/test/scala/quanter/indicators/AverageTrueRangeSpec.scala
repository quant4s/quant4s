/**
  *
  */
package quanter.indicators

import quanter.QuanterUnitSpec

/**
  *
  */
class AverageTrueRangeSpec extends QuanterUnitSpec {
  describe ("用外部文件测试 EMA") {
    it("spy_atr.txt ATR14 列") {
      val atr = new AverageTrueRange(14, MovingAverageType.Simple)
      TestHelper.testTradeBarIndicator(atr, "spy_atr.txt", "ATR14")
    }
  }
}
