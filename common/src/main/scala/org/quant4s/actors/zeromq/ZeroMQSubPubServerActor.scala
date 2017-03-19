/**
  *
  */
package org.quant4s.actors.zeromq

import java.lang.management.ManagementFactory

import scala.concurrent.duration._

import akka.actor.{Actor, Props}
import akka.serialization.SerializationExtension
import akka.util.ByteString
import akka.zeromq._

//case class PublishData(topic: String, data: String)

object ZeroMQSubPubServerActor {
  def props = Props(classOf[ZeroMQSubPubServerActor])
  val path = "datapub"
}
/**
  *
  */
class ZeroMQSubPubServerActor extends Actor {
  val pubSocket = ZeroMQExtension(context.system).newSocket(SocketType.Pub, Bind("tcp://*:8089"))
  val memory = ManagementFactory.getMemoryMXBean
  val os = ManagementFactory.getOperatingSystemMXBean
  val ser = SerializationExtension(context.system)
  import scala.concurrent.ExecutionContext.Implicits.global
  context.system.scheduler.schedule(3 seconds, 3 seconds, pubSocket, ZMQMessage(ByteString("topic"), ByteString("data")))

  override def receive: Receive = {
    case d: PublishData => {
      pubSocket ! ZMQMessage(ByteString(d.topic), ByteString(d.data))
    }

  }
}
