/**
  *
  */
package quanter.actors.zeromq

import akka.actor.{Actor, Props}
import akka.zeromq._
import akka.serialization.SerializationExtension
import java.lang.management.ManagementFactory

import akka.util.ByteString
import akka.zeromq.ZeroMQExtension

import scala.concurrent.duration._

case class PublishData(topic: String, data: String)

object ZeroMQServerActor {
  def props = Props(classOf[ZeroMQServerActor])
  val path = "datapub"
}
/**
  *
  */
class ZeroMQServerActor extends Actor {
  val pubSocket = ZeroMQExtension(context.system).newSocket(SocketType.Pub, Bind("tcp://*:8089"))
  val memory = ManagementFactory.getMemoryMXBean
  val os = ManagementFactory.getOperatingSystemMXBean
  val ser = SerializationExtension(context.system)

  override def receive: Receive = {
    case d: PublishData => {
      pubSocket ! ZMQMessage(ByteString(d.topic), ByteString(d.data))
    }
  }
}
