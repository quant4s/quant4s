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
      indicator match  {
//        case indi: IndicatorBase[TradeBar] => TestHelper.testTradeBarIndicatorReset(indi,testFileName)
//        case indi: IndicatorBase[IndicatorDataPoint] => TestHelper.testIndicatorReset(indi,testFileName)
        case _ => println("unsupported message")
      }
    }
  }

  describe("用外部数据进行比较") {
    it("reset 前后") {
      val indicator = createIndicator
      testIndicator(indicator)
      indicator.reset
      testIndicator(indicator)
    }
  }

  private def testIndicator(indicator: IndicatorBase[T]) = {
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

