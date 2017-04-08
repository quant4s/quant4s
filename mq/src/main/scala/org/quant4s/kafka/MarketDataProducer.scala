package org.quant4s.kafka

import java.util.Properties

import akka.actor.Actor
import akka.actor.Actor.Receive
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}

/**
  * 市场数据生产者
  */
class MarketDataProducer extends Actor{
  private val props = new Properties()
  props.put("metadata.broker.list", "")
  props.put("serializer.class", "kafka.serializer.StringEncoder")
  props.put("request.required.acks", "1")
  props.put("producer.type", "async")

  private val config = new ProducerConfig(this.props)
  private val producer = new Producer[String, String](this.config)

  def send(): Unit = {
//    val recenord = org.apache.kafka.clients.producer.ProducerRecord
    //    val message = new KeyedMessage[String, String]("MD.TICK", "csv")
    //    producer.sd(message)
  }

  override def receive: Receive = {
case _ =>
  }
}
