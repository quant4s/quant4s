/**
  *
  */
package quanter.actors.securitySelection

import akka.actor.{Actor, ActorLogging}
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import quanter.indicators.{IndicatorFactory, MovingAverageConvergenceDivergence}
import quanter.rest.TechIndi

/**
  *
  */
class TechIndiActor extends  Actor with ActorLogging {
  override def receive: Receive = {
    case ti: TechIndi =>_handleIndi(ti)
    case json: String => {
      implicit val formats = DefaultFormats
      val jv = parse(json)
    }
    case _ =>
  }

  def _handleIndi(cmd: TechIndi): Unit = {
    cmd.name match {
      case "MACD" => _handleMACD(cmd)

      case _ =>
    }
  }

  def _handleMACD(cmd: TechIndi): Unit = {
    // 1、计算MACD 的值, 前一个macd 值小于0
    val macd = new IndicatorFactory().createDataPointIndicator("macd", "").asInstanceOf[MovingAverageConvergenceDivergence]
    // macd.signal > 0
    // 读取常见的MACD值
    cmd.op match {
      case "GC" =>
      case "DC" =>
    }
  }
}
