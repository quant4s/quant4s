package quanter.mq

import akka.zeromq._
import akka.actor.{Actor, Props}
import akka.util.ByteString

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class Tick(topic: String, body: String)
case class PublishData(topic: String, data: String)

object DataPubActor {
  def props() = Props(classOf[DataPubActor])
}

class DataPubActor extends Actor {

  val pubSocket = ZeroMQExtension(context.system).newSocket(SocketType.Pub, Bind("tcp://*:8091"))
  override def preStart(): Unit = {
    context.system.scheduler.schedule(1 second, 1 second, self, Tick)
  }

  override def postRestart(reason: Throwable): Unit = {

  }

  def receive: Receive = {
    case t: Tick => pubSocket ! ZMQMessage(ByteString(t.topic), ByteString(t.body))
    case _ =>
  }
}
