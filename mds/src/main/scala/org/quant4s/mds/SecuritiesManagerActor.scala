package org.quant4s.mds

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.quant4s.mds.provider.csv.CsvDataWriterActor
import org.quant4s.securities.{Security, SecurityManager}

import scala.collection.mutable

/**
  * securities
  */
object SecuritiesManagerActor {
  def props = {
    Props(classOf[SecuritiesManagerActor])
  }

  val path = "securities_manager"
}

class SecuritiesManagerActor extends Actor with ActorLogging {
  val manager = new SecurityManager()
  var secActors  = new mutable.HashMap[String, ActorRef]()

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    init
  }

  /**
    *  处理的消息有
    *  1. 订阅证券
    *  2. 取消订阅证券
    *  3. 新建一个证券Actor
    * @return
    */
  override def receive: Receive = {
    case SubscriptionSymbol(symbol) => subscribe(SubscriptionSymbol(symbol))
    case UnsubscriptionSymbol(symbol) => unsubscribe(UnsubscriptionSymbol(symbol))
    case sec: Security => createSecurityActor(sec)
    case _ =>
  }

  /**
    * 订阅证券行情数据
    * @param symbol
    */
  private def subscribe(symbol: SubscriptionSymbol) = {
    log.debug("订阅证券%s数据".format(symbol))
    if(secActors.contains(symbol.symbol)) {
      secActors(symbol.symbol) forward symbol
    } else {
      log.debug("订阅证券%s数据错误，未找到相应的Actor".format(symbol))
    }
  }

  /**
    * 取消订阅证券行情数据
    * @param symbol
    */
  private def unsubscribe(symbol: UnsubscriptionSymbol) = {
    log.debug("forward unsubscription")
    if(secActors.contains(symbol.symbol)) {
      secActors(symbol.symbol) forward symbol
    }else {
      log.debug("取消订阅证券%s数据错误，未找到相应的Actor".format(symbol))
    }
  }


  /**
    * 读取文件，创建所有的证券Actor, 所有的证券CsvWriterActor
    */
  private def init = {
    for((k, v) <- manager) {
      val ref = context.actorOf(SecurityActor.props(v), k)
      secActors += ( k -> ref)

      //val writerRef = context.actorOf(CsvDataWriterActor.props(k), s"cdw${k}")
    }
  }

  private def createSecurityActor(sec: Security): Unit = {
    if(!secActors.contains(sec.symbol)) {
      val ref = context.actorOf(SecurityActor.props(sec), sec.symbol)
      secActors += (sec.symbol -> ref)
    }
  }
}
