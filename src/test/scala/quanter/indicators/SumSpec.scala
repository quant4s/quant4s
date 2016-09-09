package quanter.indicators

import java.util.Date

import quanter.QuanterUnitSpec
import quanter.indicators.window.{Sum, SimpleMovingAverage}
import  quanter.indicators.IndicatorExtensions._
import quanter.CommonExtensions._

/**
  * Created by joe on 16-3-11.
  */
class SumSpec extends QuanterUnitSpec{
  describe("测试 Sum 指标计算正确") {
    it("3个数字, 周期为2, 求和") {
      var sum = new Sum(2)
      val time = new Date()

      sum.update(time.addDays(1), 1.0)
      sum.current.value should be(1.0)

      sum.update(time.addDays(2), 2.0)
      sum.current.value should be(3.0)

      sum.update(time.addDays(3), 3.0)
      sum.current.value should be(5.0)

    }
  }

  describe("测试 Sum 指标 reset 正确") {
    it("Sum应该为默认状态") {
      var sum = new Sum(2)
      for(data <- TestHelper.getDataStream(3, null))  { // 1, 2, 3
        sum.update(data)
      }
      sum.isReady should be(true)

      sum.reset
      TestHelper.assertIndicatorIsInDefaultState(sum)
      sum.current.value should be(0.0)

      sum.update(new Date(), 1.0)
      sum.current.value should be(1.0)

    }
  }
}
