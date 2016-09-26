package quanter.actors.data

import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.Actor.Receive
import quanter.actors.securities.{SecuritiesManagerActor, SubscriptionSymbol}
import quanter.actors.zeromq.{PublishData, ZeroMQServerActor}
import quanter.data.BaseData

/**
  * 000001.XSHE,TICK
  */

object TickActor {
  def props (json: String): Props = {
    val arr = json.split(",")
    Props.create(classOf[TickActor], arr(0), json)
  }
}

class TickActor(symbol: String, topic: String) extends BaseIndicatorActor with ActorLogging {

  _init()

  override def receive: Receive = {
    case  s: BaseData => // 将数据写入MQ
      pubRef ! PublishData(topic, s.toJson)
  }

  private def _init(): Unit = {
    securitiesManagerRef ! new SubscriptionSymbol(symbol)
  }
}
