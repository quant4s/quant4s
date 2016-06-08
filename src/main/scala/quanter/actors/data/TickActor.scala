package quanter.actors.data

import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.Actor.Receive

/**
  * 000001.XSHE,TICK
  */

object TickActor {
  def props (json: String): Props = {
    val arr = json.split(",")
    Props.create(classOf[TickActor], arr(0))
  }
}

class TickActor(symbol: String) extends Actor with ActorLogging {
  override def receive: Receive = {
    case _ => // 将数据写入MQ
  }
}
