/**
  *
  */
package quanter.actors.provider.csv

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging}
import com.github.tototoshi.csv.CSVWriter
import quanter.data.market.TradeBar

import java.io.File

/**
  *
  */
class CsvDataWriterActor(symbol: String) extends Actor with ActorLogging {
  val writer = CSVWriter.open(new File(s"${symbol}.csv"))
  writer.writeRow(List("date", "open", "close", "high", "low"))

  override def receive: Receive = {
    case t: TradeBar => {
      // 把数据写入到csv 文件
      writer.writeRow(List(t.time, t.open, t.close, t.high, t.low))
    }
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    writer.close()
    super.postStop()
  }
}
