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
      for(i <- 0 until values.length) {
        lwma.update(new IndicatorDataPoint(new Date().addSeconds(i), values(i)))
      }

      val current = 1.0
      lwma.current.value should be(current +- epsilon)
    }
    it("2个周期 ") {
      val period = 2
      val lwma = new LinearWeightedMovingAverage(period)
      val values = Array[Double](1.0, 2.0)
      for(i <- 0 until values.length) {
        lwma.update(new IndicatorDataPoint(new Date().addSeconds(i), values(i)))
      }

      val current =  ((2 * 2.0) + (1 * 1.0)) / 3
      lwma.current.value should be(current +- epsilon)
    }

    it("5个周期 ") {
      val period = 5
      val lwma = new LinearWeightedMovingAverage(period)
      val values = Array[Double](77, 79, 79, 81, 83)
      for(i <- 0 until values.length) {
        lwma.update(new IndicatorDataPoint(new Date().addSeconds(i), values(i)))
        println("LWMA'S CURRENT IS %f".format(lwma.current.value))
      }
      val current  = 83 * (5.0 / 15) + 81 * (4.0 / 15) + 79 * (3.0 / 15) + 79 * (2.0 / 15) + 77 * (1.0 / 15)

      lwma.current.value should be(current +- epsilon)

    }
  }

  describe("测试 LWMA 指标 Reset") {
    it("LWMA 应该为默认状态") {
      val lwma = new ExponentialMovingAverage(3)

      for(data <- TestHelper.getDataStream(5, null))
      {
        lwma.update(data)
      }

      lwma.isReady should be(true)
      lwma.current.value should not be(0)
      lwma.samples should not be(0)

      lwma.reset

      TestHelper.assertIndicatorIsInDefaultState(lwma)
    }
  }
}
