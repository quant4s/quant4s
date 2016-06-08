package quanter.mq

import akka.zeromq._
import akka.actor.{Actor, Props}
import akka.util.ByteString

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case object Tick

object DataPubActor {
  def props() = Props(classOf[DataPubActor])
}

class DataPubActor extends Actor {

  val pubSocket = ZeroMQExtension(context.system).newSocket(SocketType.Pub, Bind("tcp://172.16.240.143:1235"))
  override def preStart(): Unit = {
    context.system.scheduler.schedule(1 second, 1 second, self, Tick)
  }

  override def postRestart(reason: Throwable): Unit = {

  }

  def receive: Receive = {
    case Tick => pubSocket ! ZMQMessage(ByteString("sid123"), ByteString("gedfgfd"))
    case _ =>
  }
}
