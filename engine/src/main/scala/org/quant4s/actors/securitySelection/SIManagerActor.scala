/**
  *
  */
package org.quant4s.actors.securitySelection

import java.io.File

import akka.actor.{Actor, ActorLogging, Props}
import com.github.tototoshi.csv.CSVReader
import org.quant4s.securities.Security
import org.quant4s.actors.SecuritySelection
import org.quant4s.securitySelection.{Instrument, Selector}

import scala.collection.mutable

/**
  * 每天都需要读取一次文件
  */
class SIManagerActor extends Actor with ActorLogging {
  var selector: Selector = _initSelector()

  override def receive: Receive = {
    case s: SecuritySelection => {
      // 创建一个选股解释器
      val ref = context.actorOf(SelectionInterpreterActor.props(s.cmds, s.topic, selector))
    }
  }

  def _initSelector() = {
    val reader = CSVReader.open(new File("stock.list.csv"))
    val symbols = reader.allWithHeaders()
    var securityManager = List[Instrument]()
    symbols.foreach(m => {
      val symbol = m("代码")
      val pe = m("市盈率").toDouble
      val pb = m("市净率").toDouble
      val capital = m("总股本").toLong
      val roe = m("净资产收益率").toDouble
      val mv = (m("收盘价").toDouble * capital).toLong
      val ins = new Instrument(symbol, pe, pb, capital, roe, mv)
      securityManager = securityManager :+ ins
    })

    new Selector(securityManager)
  }
}

object SIManagerActor {
  def props = {
    Props.create(classOf[SIManagerActor])
  }
  def path = "sim"
}
