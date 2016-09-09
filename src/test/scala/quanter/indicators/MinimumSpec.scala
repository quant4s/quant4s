package quanter.indicators

import java.util.Date

import quanter.QuanterUnitSpec
import quanter.indicators.IndicatorExtensions._
import quanter.CommonExtensions._
import quanter.indicators.window.{Minimum, Maximum}

/**
  * Created by joe on 16-3-12.
  */
class MinimumSpec extends QuanterUnitSpec {
  describe("测试 Min 指标计算正确") {
    it("5个数字, 周期为3 求最大值") {
      val min = new Minimum(3)

      val time = new Date()
      min.update(time, 1.0)
      min.current.value should be(1.0)
      min.periodsSinceMinimum should be(0)

      min.update(time.addDays(1), 2.0)
      min.current.value should be(1.0)
      min.periodsSinceMinimum should be(1)

      min.update(time.addDays(2), -1.0)
      min.current.value should be(-1.0)
      min.periodsSinceMinimum should be(0)

      min.update(time.addDays(3), 2.0)
      min.current.value should be(-1.0)
      min.periodsSinceMinimum should be(1)

      min.update(time.addDays(4), 0.0)
      min.current.value should be(-1.0)
      min.periodsSinceMinimum should be(2)

      min.update(time.addDays(5), 3.0)
      min.current.value should be(0.0)
      min.periodsSinceMinimum should be(1)

      min.update(time.addDays(6), 2.0)
      min.current.value should be(0.0)
      min.periodsSinceMinimum should be(2)
    }
  }

  describe("测试Min 指标 Reset") {
    it("Max 应该为默认状态") {
      var min = new Minimum(3)
      val time = new Date()
      min.update(time, 1.0)
      min.update(time.addSeconds(1), 2.0)
      min.update(time.addSeconds(2), 1.0)

      min.isReady should be(true)

      min.reset
      TestHelper.assertIndicatorIsInDefaultState(min)

      min.periodsSinceMinimum should be(0)
    }
  }
}
