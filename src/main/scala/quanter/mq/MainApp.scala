package quanter.mq

import akka.actor.ActorSystem

import scala.io.StdIn

/**
  * 测试
  */
object MainApp  {
  def main(args: Array[String]) = {
    val system = ActorSystem.create("server")
    val pub = system.actorOf(DataPubActor.props(), "datapub")
    println("hello, scala")
    StdIn.readLine()

    system.shutdown()
  }
}
