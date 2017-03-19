/**
  *
  */
package org.quant4s.actors.securitySelection

import java.io.File

import akka.actor.{Actor, ActorLogging}
import com.github.tototoshi.csv.CSVReader
import org.quant4s.securitySelection.{Instrument, Selector}


/**
  * 板块选股
  */
class SectorIndiActor(selector: Selector) extends Actor with ActorLogging{
  override def receive: Receive = {
    case s: String => {
      // 读取板块文件信息
      val reader = CSVReader.open(new File(s"sector/${s}.csv"))
      val symbols = reader.all()
      var securities = List[Instrument]()
      symbols.foreach(s => {
        for( ins <- selector.pool) {
          if(ins.code == s) securities = securities :+ ins
        }
      })

      sender ! new Selector(securities)
    }
  }
}
