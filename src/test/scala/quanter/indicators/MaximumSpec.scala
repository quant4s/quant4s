package quanter.indicators

import java.util.Date

import quanter.QuanterUnitSpec
import quanter.indicators.window.Maximum
import quanter.indicators.IndicatorExtensions._
import quanter.CommonExtensions._

/**
  * Created by joe on 16-3-12.
  */
class MaximumSpec extends QuanterUnitSpec {
  describe("测试 MAX 指标计算正确") {
    it("5个数字, 周期为3 求最大值") {
      val max = new Maximum(3)

      val time = new Date()
      max.update(time.addDays(1), 1.0)
      max.current.value should be(1.0)
      max.periodsSinceMaximum should be(0)

      max.update(time.addDays(2), -1.0)
      max.current.value should be(1.0)
      max.periodsSinceMaximum should be(1)

      max.update(time.addDays(3), 0.0)
      max.current.value should be(1.0)
      max.periodsSinceMaximum should be(2)

      max.update(time.addDays(4), -2.0)
      max.current.value should be(0.0)
      max.periodsSinceMaximum should be(1)

      max.update(time.addDays(5), -2.0)
      max.current.value should be(0.0)
      max.periodsSinceMaximum should be(2)
    }
  }

  describe("测试Max 指标 Reset") {
    it("Max 应该为默认状态") {
      var max = new Maximum(3)
      val time = new Date()
      max.update(time, 1.0)
      max.update(time.addSeconds(1), 2.0)
      max.update(time.addSeconds(2), 1.0)

      max.isReady should be(true)

      max.reset
      TestHelper.assertIndicatorIsInDefaultState(max)

      max.periodsSinceMaximum should be(0)
    }
  }
}
