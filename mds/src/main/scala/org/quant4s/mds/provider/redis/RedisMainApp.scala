/**
  *
  */
package org.quant4s.mds.provider.redis

import java.net.InetSocketAddress

import akka.actor.Props
import redis.RedisClient

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
/**
  *
  */
object RedisMainApp extends App{
  implicit val akkaSystem = akka.actor.ActorSystem()

  val redis = RedisClient(host = "172.16.240.1", password=Some("1"))

  akkaSystem.scheduler.schedule(2 seconds, 2 seconds)(redis.publish("time", "时间为：" + System.currentTimeMillis()))
  akkaSystem.scheduler.schedule(2 seconds, 5 seconds)(redis.publish("pattern.match", "模式为：pattern value"))
  akkaSystem.scheduler.scheduleOnce(20 seconds)(akkaSystem.shutdown())

  val channels = Seq("time")
  val patterns = Seq("pattern.*")
  akkaSystem.actorOf(Props(classOf[RedisDataProviderActor]))

//  redis.publish("time", "时间为：" + System.currentTimeMillis())
//  redis.publish("pattern.match", "模式为：pattern value")
  println("按任何按钮退出......")
  val s = scala.io.StdIn.readLine()
}
