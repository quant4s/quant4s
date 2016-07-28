package quanter.actors.data

import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.Actor.Receive
import quanter.actors.securities.{SecuritiesManagerActor, SubscriptionSymbol}
import quanter.actors.ws.WebSocketActor

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
  val securitiesManagerRef = context.actorSelection("/user/" + SecuritiesManagerActor.path)
  val wsRef = context.actorSelection("/user/" + WebSocketActor.path)

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    _init()
  }


  override def receive: Receive = {
    case  s: String => // 将数据写入MQ
      wsRef ! s
  }

  private def _init(): Unit = {
    securitiesManagerRef ! new SubscriptionSymbol(symbol)
  }
}
