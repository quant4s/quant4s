/**
  *
  */
package quanter.actors.provider.csv

import java.io.File

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSelection}
import com.github.tototoshi.csv.CSVReader
import quanter.actors.provider.{AskListenedSymbol, DataProviderActor, Execute, QuerySnapData}

import scala.concurrent.duration._
import scala.collection.mutable.ArrayBuffer

/**
  *
  */
class CsvDataProviderActor extends DataProviderActor {

  override def receive: Receive =  {
    case ask: AskListenedSymbol => addSymbol(ask.symbol)
    case query: QuerySnapData => _querySnapData()
    case Execute => _run()
    case _ =>
  }

  var openFiles = new ArrayBuffer[CSVReader]()
  private def _run(): Unit = {
    context.system.scheduler.schedule(0 seconds, 10 milliseconds, self, new QuerySnapData())
    // 打开所有的csv 文件
    for(key <- symbolSelections.keys) {
      val file = new File(key + ".csv")
      val reader = CSVReader.open(file)
      openFiles += reader
      reader.readNext() // 读取标题行
      // val symbols = reader.allWithHeaders()
    }

    // 对历史行情 轮流发送到Actor
    for(reader <- openFiles) {
      val line = reader.readNext()
      // 发送数据到关注的Actor
    }
  }
}
