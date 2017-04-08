/**
  *
  */
package org.quant4s.indicators

import java.util.Date

import org.quant4s.QuanterUnitSpec
import org.quant4s.indicators.window.ExponentialMovingAverage
import org.quant4s.CommonExtensions._
import org.quant4s.indicators.IndicatorExtensions._

/**
  *
  */
class ExponentialMovingAverageSpec extends QuanterUnitSpec {
  epsilon = 0.01
  describe("测试指标是否正确") {
    it("测试4周期的数据") {
      val period = 4
      val values: Array[Double] = Array(1.0, 10.0, 100.0, 1000.0)
      val expFactor = 2.0/(1.0 + period)

      val ema4 = new ExponentialMovingAverage(period)

      var current = 0.0
      for(i <- 0 until values.length) {
        ema4.update(new IndicatorDataPoint(new Date().addSeconds(i), values(i)))
        current = if(i == 0) values(i)
        else values(i) * expFactor + (1-expFactor) * current
          ema4.current.value should be(current +- 0.001)
      }

    }
  }
  describe ("用外部文件测试 EMA") {
    it("spy_with_indicators.txt EMA14 列") {
//      val ema = new ExponentialMovingAverage(14)
//      TestHelper.testIndicator(ema, "EMA14", epsilon)
      cancel("需要检测测试数据")
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
