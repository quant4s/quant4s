package quanter.actors.securities

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import quanter.data.market.SnapData
import quanter.securities.Security

import scala.collection.mutable.ArrayBuffer

object SecurityActor {
  def props (sec: Security): Props = {
    Props.create(classOf[SecurityActor], sec)
  }
}

/**
  * 证券Actor实例，将报价送到订阅的actor
  */
class SecurityActor(security: Security) extends Actor with ActorLogging{
  private val _subscribers = ArrayBuffer[ActorRef]()

  override def receive: Receive = {
    case SubscriptionSymbol(symbol) => _subscribe(symbol)
    case UnsubscriptionSymbol(symbol) => _unsubscribe(symbol)
    case data: SnapData => _dataArrived(data)
  }

  private def _subscribe(symbol: String) = {
    log.info("订阅 %s's price".format(symbol))
    _subscribers += sender
  }

  private def _unsubscribe(symbol: String) = {
    log.info("取消订阅 %s's price".format(symbol))
    _subscribers -= sender
  }

  private def _dataArrived(data: SnapData): Unit = {
    for(suber <- _subscribers)
      suber ! data
  }
}

