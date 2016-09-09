package quanter.indicators

import java.io.File
import java.util.{Calendar, Date}

import com.github.tototoshi.csv.CSVReader
import org.scalatest.Matchers
import quanter.data.market.TradeBar

import scala.collection.mutable.ArrayBuffer

/**
  * Created by joe on 16-3-10.
  */
object TestHelper extends Matchers{
  def getDataStream(pcount: Int, pvalueProducer: (Int) => Double) = {
    val calendar = Calendar.getInstance()
    val valueProducer =  if (pvalueProducer == null) (x: Int) => (x+0.0) else pvalueProducer

    val ret = for (i <- 1 to pcount)yield {
      calendar.add(Calendar.SECOND, 1)
      new IndicatorDataPoint(calendar.getTime(), valueProducer(i))
    }
    ret
  }

  def shouldBeInDefaultState(indicator: IndicatorBase[IndicatorDataPoint]) = {
    indicator.current.value should be(0.0)
    // indicator.current.time should be()
    indicator.samples should be(0)
    indicator.isReady should be(false)

    // TODO: 递归验证indicator 的子indicator
  }

  def testTradeBarIndicatorReset(indicator: IndicatorBase[TradeBar], externalDataFilename: String) = {
    for(bar <- getTradeBarStream(externalDataFilename))
      indicator.update(bar)

    indicator.isReady should be(true)
  }

  def testTradeBarIndicator(indicator: IndicatorBase[TradeBar], externalDataFilename: String, targetColumn: String, customAssertion: ((IndicatorBase[TradeBar], Double) => Unit)) = {
    val reader = CSVReader.open(new File(externalDataFilename))
    val lines = reader.allWithHeaders()
    var tradeBars = ArrayBuffer[TradeBar]()
    lines.foreach(m => {
      val tradebar = new TradeBar(){
        high = m("high").toDouble
        low = m("low").toDouble
        open = m("open").toDouble
        close = m("close").toDouble
      }
      indicator.update(tradebar)

      if(indicator.isReady) {
        val expected = m(targetColumn).toDouble
        customAssertion.apply(indicator, expected)
      }
    })

  }
//  public static void TestIndicator(IndicatorBase<TradeBar> indicator, string externalDataFilename, string targetColumn, Action<IndicatorBase<TradeBar>, double> customAssertion)
//  {
//    bool first = true;
//    int targetIndex = -1;
//    bool fileHasVolume = false;
//    foreach (var line in File.ReadLines(Path.Combine("TestData", externalDataFilename)))
//    {
//      var parts = line.Split(',');
//      if (first)
//      {
//        fileHasVolume = parts[5].Trim() == "Volume";
//        first = false;
//        for (int i = 0; i < parts.Length; i++)
//        {
//          if (parts[i].Trim() == targetColumn)
//          {
//            targetIndex = i;
//            break;
//          }
//        }
//        continue;
//      }
//

//
//      if (!indicator.IsReady || parts[targetIndex].Trim() == string.Empty)
//      {
//        continue;
//      }
//
//      double expected = double.Parse(parts[targetIndex], CultureInfo.InvariantCulture);
//      customAssertion.Invoke(indicator, expected);
//    }



  def testIndicatorReset(indicator: IndicatorBase[IndicatorDataPoint], externalDataFilename: String) = {
    // var date = DateTime.Today;
    //    foreach (var data in GetTradeBarStream(externalDataFilename, false))
    //    {
    //      indicator.Update(date, data.Close);
    //    }
    //
    //    Assert.IsTrue(indicator.IsReady);
    //
    //    indicator.Reset();
    //
    //    AssertIndicatorIsInDefaultState(indicator);
  }

  def getTradeBarStream(externalDataFilename: String) = {
    val reader = CSVReader.open(new File(externalDataFilename))
    val lines = reader.allWithHeaders()
    var tradeBars = ArrayBuffer[TradeBar]()
    lines.foreach(m => {
      tradeBars += new TradeBar(){
        high = m("high").toDouble
        low = m("low").toDouble
        open = m("open").toDouble
        close = m("close").toDouble
      }
    })
    tradeBars
  }
}

