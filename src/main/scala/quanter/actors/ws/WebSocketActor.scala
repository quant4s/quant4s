/**
  *
  */
package quanter.actors.ws

import akka.actor.{Actor, ActorLogging, Props}

/**
  *
  */
class WebSocketActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case s: String => {
      log.debug(s"开始写数据${s}到websocket")
    }
  }
}

object WebSocketActor {
  def props : Props = {
    Props(classOf[WebSocketActor])
  }

  val path = "ws"
}

