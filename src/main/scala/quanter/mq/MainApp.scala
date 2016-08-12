package quanter.mq

import akka.actor.ActorSystem
import quanter.actors.zeromq.ZeroMQServerActor

import scala.io.StdIn

/**
  * 测试
  */
object MainApp  {
  def main(args: Array[String]) = {
    val system = ActorSystem.create("server")
//    val pub = system.actorOf(DataPubActor.props(), "datapub")
    val pub = system.actorOf(ZeroMQServerActor.props(), "datapub")
    println("hello, scala")
    StdIn.readLine()

    system.shutdown()
  }
}
