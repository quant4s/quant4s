/**
  *
  */
package quanter.actors.provider.csv

import java.io.File

import akka.actor.ActorSelection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import com.github.tototoshi.csv.CSVReader
import quanter.actors.AskListenedSymbol
import quanter.actors.provider.{DataProviderActor, Execute, QuerySnapData}
import quanter.data.market.TradeBar
import quanter.CommonExtensions._

import scala.collection.mutable

/**
  * 提供历史数据
  */
class CsvDataProviderActor extends DataProviderActor {

  override def receive: Receive =  {
    case ask: AskListenedSymbol => addSymbol(ask.symbol)
    case query: QuerySnapData => _querySnapData()
    case Execute => _run()
    case _ =>
  }

  var openFiles = new mutable.HashMap[CSVReader, ActorSelection]
  // var openFiles = new ArrayBuffer[CSVReader]()
  private def _run(): Unit = {
    // 打开所有的csv 文件
    for(ss <- symbolSelections) {
      val file = new File(ss._1 + ".csv")
      val reader = CSVReader.open(file)
      openFiles.put(reader, ss._2)
      reader.readNext() // 读取标题行
    }

    context.system.scheduler.schedule(0 seconds, 10 milliseconds, self, new QuerySnapData())

  }

  private def _querySnapData(): Unit = {
    // 对历史行情 轮流发送到Actor
    for(reader <- openFiles) {
      val line = reader._1.readNext()
      // 发送数据到关注的Actor
      if(line != None) {
        val data = new TradeBar()
        data.open = line.get(1).toDouble
        data.high = line.get(2).toDouble
        data.low = line.get(3).toDouble
        data.close = line.get(4).toDouble
        data.volume = line.get(5).toLong
        data.time = line.get(0).toDate("yyyy-MM-dd")
        reader._2 ! data
      }
    }
  }


}
