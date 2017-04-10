/**
  *
  */
package org.quant4s.zeromq

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.ByteString
import akka.zeromq.{Bind, SocketType, ZMQMessage, ZeroMQExtension}

/**
  *
  */
class ZeroMQReqRspServerActor extends Actor with ActorLogging{
  val repSocket = ZeroMQExtension(context.system).newSocket(SocketType.Rep, Bind("tcp://*:8088"))
  override def receive: Receive = {
    case msg: ZMQMessage => {
      _dispatchMessage(msg)
    }
  }

  /**
    *
    * @param msg frame(0)的取值有
    */
  private def _dispatchMessage(msg: ZMQMessage): Unit = {
    log.debug("[_handleMessage]处理Req消息")
    msg.frame(0).decodeString("utf8") match {
      case "shakeHand" =>
      case "login" =>
      case _ => sender ! ZMQMessage(ByteString("topic"), ByteString("message"))
    }
  }

  private def _handleShakeHand(msg: ZMQMessage, topic: String, body: String): Unit = {
    val session  = "12345"

    sender ! ZMQMessage(msg.frame(0), ByteString(session))
  }

  private def _handleLogin(msg: ZMQMessage): Unit = {
    val token = ""
    sender ! ZMQMessage(msg.frame(0), ByteString(token))
  }

  private def _handleReqStrategy(msg: ZMQMessage): Unit = {
    val token = msg.frame(0)
    val jsonRet = ""
    sender ! ZMQMessage(msg.frame(0), ByteString(jsonRet))
  }
}

object ZeroMQReqRspServerActor {
  def props = Props(classOf[ZeroMQReqRspServerActor])
  val path = "datarsp"
}