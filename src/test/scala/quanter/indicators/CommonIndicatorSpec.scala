package quanter.indicators

import java.io.File

import com.github.tototoshi.csv.CSVReader
import quanter.data.BaseData
import quanter.QuanterUnitSpec
import quanter.data.market.TradeBar


import scala.collection.mutable.ArrayBuffer

/**
  * Created by joe on 16-3-15.
  */
abstract class CommonIndicatorSpec[T <: BaseData] extends QuanterUnitSpec {
  describe("测试指标 Reset") {
    it("测试reset") {
      val indicator = createIndicator
      if(indicator.isInstanceOf[IndicatorBase[TradeBar]]) TestHelper.testTradeBarIndicatorReset(indicator.asInstanceOf[ IndicatorBase[TradeBar]],testFileName)
      else if(indicator.isInstanceOf[IndicatorBase[IndicatorDataPoint]]) TestHelper.testIndicatorReset(indicator.asInstanceOf[IndicatorBase[IndicatorDataPoint]],testFileName)
      else println("不支持的指标")
    }
  }

  describe("用外部数据文件进行测试") {
    it("测试， reset，测试") {
      val indicator = createIndicator
      TestHelper.testTradeBarIndicatorReset(indicator.asInstanceOf[ IndicatorBase[TradeBar]],testFileName)
//      testIndicator(indicator)
      indicator.reset
      TestHelper.testTradeBarIndicatorReset(indicator.asInstanceOf[ IndicatorBase[TradeBar]],testFileName)
//      testIndicator(indicator)
    }
  }

  private def testIndicator(indicator: IndicatorBase[T]) = {
    if(indicator.isInstanceOf[IndicatorBase[TradeBar]]) TestHelper.testTradeBarIndicator(indicator.asInstanceOf[ IndicatorBase[TradeBar]],testFileName, testColumnName, (indi, expected) => indi.current.value should be (expected +- epsilon))
    else if(indicator.isInstanceOf[IndicatorBase[IndicatorDataPoint]]) TestHelper.testIndicator(indicator.asInstanceOf[IndicatorBase[IndicatorDataPoint]],testFileName, testColumnName, (indi, expected) => indi.current.value should be (expected +- epsilon))
    else println("不支持的指标")
  }

  protected def createIndicator: IndicatorBase[T]
  protected def testFileName: String
  protected def testColumnName: String


}

