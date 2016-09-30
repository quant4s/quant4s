/**
  *
  */
package quanter.actors.securitySelection

import akka.actor.{Actor, ActorLogging}
import quanter.rest.TechIndi

/**
  *
  */
class TechIndiActor extends  Actor with ActorLogging {
  override def receive: Receive = {
    case ti: TechIndi =>_handleIndi(ti)
    case _ =>
  }

  def _handleIndi(cmd: TechIndi): Unit = {
    cmd.name match {
      case "MACD" => _handleMACD(cmd)

      case _ =>
    }
  }

  def _handleMACD(cmd: TechIndi): Unit = {
    // 1、计算MACD 的值
    // 读取常见的MACD值
    cmd.op match {
      case "GC" =>
      case "DC" =>
    }
  }
}
