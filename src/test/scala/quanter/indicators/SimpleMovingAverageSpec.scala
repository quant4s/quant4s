package quanter.indicators

import java.util
import java.util.Date

import quanter.QuanterUnitSpec
import quanter.indicators.window.{SimpleMovingAverage, Sum}
import  quanter.indicators.IndicatorExtensions._
import quanter.CommonExtensions._

import scala.collection.mutable.ArrayBuffer

/**
  * Created by joe on 16-3-12.
  */
class SimpleMovingAverageSpec extends QuanterUnitSpec{
  describe("测试 SMA 指标计算正确") {
    it("3个数字, 周期为2, 求和") {
      val sma = new SimpleMovingAverage(4)
      val data = Array(1.0, 10.0, 100.0, 1000.0, 10000.0, 1234.0, 56789.0)

      val seen = new ArrayBuffer[Double]()
      val time = new Date()
      var i = 0
      for(d <- data) {
        i += 1
        seen += d
        sma.update(new IndicatorDataPoint(time.addSeconds(i), d))
        val v = (seen.reverse.take(sma.period).sum / math.min(sma.period, seen.size))
        sma.current.value should be(v)
      }
    }
  }

  describe("测试 SMA 指标 reset 正确") {
    it("SMA 应该为默认状态") {
      var sma = new SimpleMovingAverage(2);
      for(data <- TestHelper.getDataStream(3, null))  { // 1, 2, 3
        sma.update(data)
      }
      sma.isReady should be(true)

      sma.reset
      TestHelper.assertIndicatorIsInDefaultState(sma)
      sma.current.value should be(0.0)

      sma.update(new Date(), 1.0)
      sma.current.value should be(1.0)

    }
  }

  describe ("用外部文件测试") {
    it("spy_with_indicators.txt SMA14 列") {
      val sma = new SimpleMovingAverage(14)
      TestHelper.testIndicator(sma, "SMA14", 0.01)
    }
  }
}
