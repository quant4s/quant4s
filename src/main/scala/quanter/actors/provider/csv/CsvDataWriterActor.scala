/**
  *
  */
package quanter.actors.provider.csv

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, Props}
import com.github.tototoshi.csv.CSVWriter
import org.quant4s.data.market.TradeBar
import java.io.File

import quanter.securities.Security

/**
  *
  */
class CsvDataWriterActor(symbol: String) extends Actor with ActorLogging {
  val writer = _getWriter()
  var _previous: TradeBar = null
//  writer.writeRow(List("date", "open", "close", "high", "low"))

  private def _getWriter(): CSVWriter = {
    val f = new File(s"./daily/${symbol}.csv")
    var w: CSVWriter = null
    if(!f.exists()) {
      f.createNewFile()
      w = CSVWriter.open(new File(s"./daily/${symbol}.csv"), true)
      w.writeRow(List("date", "open", "close", "high", "low"))
    } else
      w = CSVWriter.open(new File(s"./daily/${symbol}.csv"), true)

    w
  }
  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
  }

  override def receive: Receive = {
    case t: TradeBar => {
      // 把数据写入到csv 文件
      if(_previous == null) {
        writer.writeRow(List(t.time, t.open, t.close, t.high, t.low))
      } else  if(_previous.time.before(t.time)){
        writer.writeRow(List(t.time, t.open, t.close, t.high, t.low))
      }
      _previous = t

    }
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    writer.close()
    super.postStop()
  }
}

object CsvDataWriterActor {
  def props (sec: String): Props = {
    Props.create(classOf[CsvDataWriterActor], sec)
  }
}
