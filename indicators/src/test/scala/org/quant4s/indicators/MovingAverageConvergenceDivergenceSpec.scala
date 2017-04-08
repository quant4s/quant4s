package org.quant4s.indicators

import org.quant4s.QuanterUnitSpec
import org.quant4s.indicators.window.SimpleMovingAverage
import org.quant4s.indicators.IndicatorExtensions._

/**
  * Created by joe on 16-3-9.
  */
class MovingAverageConvergenceDivergenceSpec extends QuanterUnitSpec{
  describe("测试MACD 指标计算") {
    it("compution must be correctly") {
      val fast = new SimpleMovingAverage(3)
      val slow = new SimpleMovingAverage(5)
      val signal = new SimpleMovingAverage(3)
      val macd = new MovingAverageConvergenceDivergence("macd", 3, 5, 3, MovingAverageType.Simple)

      for (data <- TestHelper.getDataStream(7, null)) {
        println ("time:" + data.time + "   value:" + data.value)
        fast.update(data)
        slow.update(data)
        macd.update(data)

        println("fast:" + fast.current.value + "    slow:" + slow.current.value + "    macd:" + macd.current.value)

        (fast - slow) should be(macd.current.value)

        if (fast.isReady && slow.isReady) {
          signal.update(data.time, macd)
          signal.current.value should be(macd.current.value)
        }
      }
    }
  }

  describe("测试MACD 指标reset") {
    it("MACD 应该为默认状态") {
      val macd = new MovingAverageConvergenceDivergence("macd", 3, 5, 3)
      for(data <- TestHelper.getDataStream(30, null))  {
        macd.update(data)
      }
      macd.isReady should be(true)

      macd.reset

      TestHelper.assertIndicatorIsInDefaultState(macd)
      TestHelper.assertIndicatorIsInDefaultState(macd.fast)
      TestHelper.assertIndicatorIsInDefaultState(macd.slow)
      TestHelper.assertIndicatorIsInDefaultState(macd.signal)
    }
  }

}
