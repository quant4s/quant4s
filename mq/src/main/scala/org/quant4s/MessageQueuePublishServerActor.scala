package org.quant4s

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.ByteString
import org.quant4s.kafka.KafkaPublisherActor

/**
  * 封装了具体的消息队列，可以采用不同的消息队列实现
  */
class MessageQueuePublishServerActor extends Actor with ActorLogging {
  val pubsocket: ActorRef = initMQ

  def initMQ(): ActorRef = {
    context.actorOf(KafkaPublisherActor.props, KafkaPublisherActor.path)
  }

  override def receive: Receive = {
    case d: PublishData => {  // 转发到实际的MQ 实现
      pubsocket forward d
    }

  }
}

object MessageQueuePublishServerActor {
  def props(): Unit = {
    Props(classOf[MessageQueuePublishServerActor])
  }

  val path = "message_queue_publish_server"
}

case class PublishData(topic: String, data: String)
