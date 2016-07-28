package quanter.actors.securities

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import quanter.securities.{Security, SecurityManager}

import scala.collection.mutable

/**
  * securities
  */

object SecuritiesManagerActor {
  def props = {
    Props(classOf[SecuritiesManagerActor])
  }

  val path = "SEC_MANAGER"
}

class SecuritiesManagerActor extends Actor with ActorLogging {
  val manager = new SecurityManager()
  var secActors  = new mutable.HashMap[String, ActorRef]()

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    _init
  }

  /**
    *  chuli dingyue
    * @return
    */
  override def receive: Receive = {
    case SubscriptionSymbol(symbol) => _subscribe(SubscriptionSymbol(symbol))
    case UnsubscriptionSymbol(symbol) => _unsubscribe(UnsubscriptionSymbol(symbol))
    case _ =>
  }

  private def _subscribe(symbol: SubscriptionSymbol) = {
    log.debug("forward subscription")
    secActors(symbol.symbol).forward()
  }

  private def _unsubscribe(symbol: UnsubscriptionSymbol) = {
    log.debug("forward unsubscription")
    secActors(symbol.symbol).forward()
  }


  /**
    * 读取文件，创建所有的证券Actor
    */
  private def _init = {
    for((k, v) <- manager) {
      val ref = context.actorOf(SecurityActor.props(v), k)
      secActors += ( k -> ref)
      log.debug(s"初始化股票${k}Actor")
    }
  }
}
