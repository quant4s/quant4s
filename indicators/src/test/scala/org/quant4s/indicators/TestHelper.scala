package org.quant4s.indicators

import java.io.File
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.github.tototoshi.csv.CSVReader
import org.scalatest.Matchers
import quanter.data.BaseData
import org.quant4s.data.market.TradeBar
import quanter.indicators.IndicatorExtensions._

import scala.collection.mutable.ArrayBuffer

/**
  *
  */
object TestHelper extends Matchers{
  def getDataStream(pcount: Int, pvalueProducer: (Int) => Double = null) = {
    val calendar = Calendar.getInstance()
    val valueProducer =  if (pvalueProducer == null) (x: Int) => (x+0.0) else pvalueProducer

    val ret = for (i <- 1 to pcount)yield {
      calendar.add(Calendar.SECOND, 1)
      new IndicatorDataPoint(calendar.getTime, valueProducer(i))
    }
    ret
  }

  def assertIndicatorIsInDefaultState[T <: BaseData](indicator: IndicatorBase[T]) = {
    indicator.current.value should be(0.0)
    // indicator.current.time should be()
    indicator.samples should be(0)
    indicator.isReady should be(false)

    // TODO: 递归验证indicator 的子indicator
  }

  def testTradeBarIndicator(indicator: IndicatorBase[TradeBar], externalDataFilename: String, targetColumn: String, customAssertion: ((IndicatorBase[TradeBar], Double) => Unit)) = {
    val reader = CSVReader.open(new File("datas/" + externalDataFilename))
    val lines = reader.allWithHeaders()
    lines.foreach(m => {
      val tradebar = new TradeBar(){
        high = m("High").toDouble
        low = m("Low").toDouble
        open = m("Open").toDouble
        close = m("Close").toDouble
        val format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss") //3/8/2013 12:00:00 AM
        time = format.parse(m("Date"))
        if (lines.head.get("Volume") != None) volume = BigDecimal.apply(m("Volume")).toLong

      }
      indicator.update(tradebar)

      if(indicator.isReady && (m(targetColumn).trim() != "")) {
        val expected = m(targetColumn).toDouble
        customAssertion.apply(indicator, expected)
      }
    })
  }

  def testIndicator(indicator: IndicatorBase[IndicatorDataPoint], externalDataFilename: String, targetColumn: String, customAssertion: ((IndicatorBase[IndicatorDataPoint], Double) => Unit)): Unit = {
    val reader = CSVReader.open(new File("datas/" + externalDataFilename))
    val lines = reader.allWithHeaders()

    lines.foreach(m => {

      val format = new java.text.SimpleDateFormat("MM/dd/yyyy")
      val date = format.parse(m("Date"))
      val data = new IndicatorDataPoint(date, m("Close").toDouble)
      indicator.update(data)

      if(indicator.isReady && (m(targetColumn).trim() != "")) {
        val expected = m(targetColumn).toDouble
        customAssertion.apply(indicator, expected)
      }
    })

  }

  def testIndicator(indicator: IndicatorBase[IndicatorDataPoint] , targetColumn: String, epsilon: Double = 0.001)
  {
    testIndicator(indicator, "spy_with_indicators.txt", targetColumn, (i, expected) => i.current.value should be(expected +- epsilon))
  }

  def testTradeBarIndicator(indicator: IndicatorBase[TradeBar], externalDataFilename: String, targetColumn: String, epsilon: Double = 1e-3)
  {
    testTradeBarIndicator(indicator, externalDataFilename, targetColumn, (i, expected) => i.current.value should be(expected +- epsilon))
  }

//  def testIndicator[T](indicator: T, externalFilename: String, targetColumn: String, epsilon: Double = 1e-3): Unit = {
//  }

  def testTradeBarIndicatorReset(indicator: IndicatorBase[TradeBar], externalDataFilename: String) = {
    for(bar <- getTradeBarStream(externalDataFilename))
      indicator.update(bar)

    indicator.isReady should be(true)
    indicator.reset
    assertIndicatorIsInDefaultState(indicator)
  }

  def testIndicatorReset(indicator: IndicatorBase[IndicatorDataPoint], externalDataFilename: String) = {
    val date = new Date()
    for(bar <- getTradeBarStream(externalDataFilename))
      indicator.update(date, bar.close)

    indicator.isReady should be(true)

    indicator.reset
    assertIndicatorIsInDefaultState(indicator)

  }

  def getTradeBarStream(externalDataFilename: String) = {
    val reader = CSVReader.open(new File("datas/" + externalDataFilename))
    val lines = reader.allWithHeaders()
    var tradeBars = ArrayBuffer[TradeBar]()
    lines.foreach(m => {
      tradeBars += new TradeBar(){
        high = m("High").toDouble
        low = m("Low").toDouble
        open = m("Open").toDouble
        close = m("Close").toDouble
      }
    })
    tradeBars
  }

}

