/**
  *
  */
package quanter.indicators

import java.util.Date

import quanter.QuanterUnitSpec
import quanter.indicators.IndicatorExtensions._
import quanter.CommonExtensions._
/**
  *
  */
class RelativeStrengthIndexSpec extends QuanterUnitSpec  {


  describe("测试 RSI 指标 reset 正确") {
    it("RSI 应该为默认状态") {
      val rsi = new RelativeStrengthIndex(2)

      rsi.update(new Date(), 1.0)
      rsi.update(new Date().addSeconds(1), 2.0)
      rsi.isReady should be(false)
      rsi.reset

      TestHelper.assertIndicatorIsInDefaultState(rsi)
      TestHelper.assertIndicatorIsInDefaultState(rsi.averageGain)
      TestHelper.assertIndicatorIsInDefaultState(rsi.averageLoss)
    }
  }
  describe ("用外部文件测试") {
    it("spy_with_indicators.txt RSI14 列") {
      val rsi = new RelativeStrengthIndex("rsi", 14, MovingAverageType.Simple)
//      TestHelper.testIndicator(rsi, "RSI14")
      cancel("需要检测测试数据")
    }
  }
}
