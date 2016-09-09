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
//      indicator match  {
//          // TODO: 泛型化的信息被擦除
//        case indi: IndicatorBase[TradeBar] => TestHelper.testTradeBarIndicatorReset(indi,testFileName)
//        case indi: IndicatorBase[IndicatorDataPoint] => TestHelper.testIndicatorReset(indi,testFileName)
//        case _ => println("unsupported message")
//      }
    }
  }

  describe("用外部数据文件进行测试") {
    it("reset 前后") {
      val indicator = createIndicator
      testIndicator(indicator)
      indicator.reset
      testIndicator(indicator)
    }
  }

  private def testIndicator(indicator: IndicatorBase[T]) = {
    if(indicator.isInstanceOf[IndicatorBase[TradeBar]]) TestHelper.testTradeBarIndicator(indicator.asInstanceOf[ IndicatorBase[TradeBar]],testFileName, testColumnName, (indi, expected) => math.abs(indi.current.value -expected) should be <= 0.0001)
    else if(indicator.isInstanceOf[IndicatorBase[IndicatorDataPoint]]) TestHelper.testIndicator(indicator.asInstanceOf[IndicatorBase[IndicatorDataPoint]],testFileName, testColumnName, (indi, expected) => math.abs(indi.current.value -expected) should be <= 0.0001)
    else println("不支持的指标")
    indicator match  {
//      case indi: IndicatorBase[TradeBar] => TestHelper.testTradeBarIndicator(indi, testFileName, testColumnName, (indicator, expected) => indicator.current.value should be(expected))
      //        case indi: IndicatorBase[IndicatorDataPoint] => TestHelper.testIndicator(indi,testFileName)
      case _ => println("unsupported message")
    }
  }

  protected def createIndicator: IndicatorBase[T]
  protected def testFileName: String
  protected def testColumnName: String


}

