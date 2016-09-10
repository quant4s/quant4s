/**
  *
  */
package quanter.indicators

import java.util.Date

import quanter.QuanterUnitSpec
import quanter.indicators.window.{ExponentialMovingAverage, LinearWeightedMovingAverage}
import quanter.CommonExtensions._
/**
  *
  */
class LinearWeightedMovingAverageSpec extends QuanterUnitSpec{
  describe("测试 LWMA 指标计算正确") {
    it("1个周期 ") {
      val period = 1
      val lwma = new LinearWeightedMovingAverage(period)
      val values = Array[Double](1)
      for(v <- values)
        lwma.update(new IndicatorDataPoint(new Date().addSeconds(1),v ))

      val current = 1.0
      lwma.current.value should be(current)
    }
    it("2个周期 ") {
      val period = 2
      val lwma = new LinearWeightedMovingAverage(period)
      val values = Array[Double](1.0, 2.0)
      for(v <- values)
        lwma.update(new IndicatorDataPoint(new Date().addSeconds(1),v ))

      val current =  ((2 * 2.0) + (1 * 1.0)) / 3
      lwma.current.value should be(current)
    }
    it("4个周期 ") {
    }
    it("5个周期 ") {
    }
  }
}
