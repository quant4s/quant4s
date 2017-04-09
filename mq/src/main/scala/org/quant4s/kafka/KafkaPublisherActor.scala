package org.quant4s.kafka

import java.util.Properties

import akka.actor.{Actor, ActorLogging, Props}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.quant4s.PublishData

/**
  * Created by joe on 2017/4/8.
  */
class KafkaPublisherActor extends Actor with ActorLogging {
  val props = new Properties()
  val producer = new KafkaProducer[String, String](props)

  override def receive: Receive = {
    case d: PublishData => {
      val data = new ProducerRecord[String, String](d.topic, d.data)
      producer.send(data)
    }
  }

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
    producer.close()
  }
}

object KafkaPublisherActor {
  def props = {
    Props(classOf[KafkaPublisherActor])
  }

  val path = "kafka-publisher"
}
