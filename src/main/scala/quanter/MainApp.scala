package quanter

import akka.actor.ActorSystem
import akka.io.IO
import quanter.actors.receivers.{AskListenedSymbol, Execute, SinaL1Actor}
import quanter.actors.securities.SecuritiesManagerActor
import quanter.rest.{HttpServer, HttpServer$}
import spray.can.Http
import spray.routing.SimpleRoutingApp

import scala.io.StdIn

/**
  * 启动入口
  */
object MainApp extends App {
  implicit val system = ActorSystem("server-system")

  val httpServer = system.actorOf(HttpServer.props())

  // 启动REST 服务
  IO(Http) ! Http.Bind(httpServer, "127.0.0.1", port = 888)

//  val ref = system.actorOf(SinaL1Actor.props())
//  val manager = system.actorOf(SecuritiesManagerActor.props(), SecuritiesManagerActor.Path)
//
//  ref ! new AskListenedSymbol("000001.XSHE")
//  ref ! new Execute()

  // TODO: 检测 服务是否启动完毕
  StdIn.readLine()
  system.shutdown()
}
