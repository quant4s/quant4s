package quanter.actors.securities

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

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
  }

  private def _subscribe(symbol: String) = {
    log.debug("订阅 %s's price".format(symbol))
    _subscribers += sender
  }

  private def _unsubscribe(symbol: String) = {
    log.debug("取消订阅 %s's price".format(symbol))
    _subscribers -= sender
  }
}

