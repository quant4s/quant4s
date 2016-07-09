package quanter

import akka.actor.ActorSystem
import akka.io.IO
import quanter.actors.data.DataManagerActor
import quanter.actors.persistence.PersistenceActor
import quanter.actors.receivers.SinaL1Actor
import quanter.actors.strategies.StrategiesManagerActor
import quanter.actors.trade.TradeRouteActor
import quanter.rest.HttpServer
import spray.can.Http

import scala.io.StdIn

/**
  * 启动入口
  */
object MainApp extends App {
  implicit val system = ActorSystem("server-system")

  val httpServer = system.actorOf(HttpServer.props())
//  system.actorOf(SecuritiesManagerActor.props(), SecuritiesManagerActor.path)
//  system.actorOf(SinaL1Actor.props, SinaL1Actor.path)
  system.actorOf(StrategiesManagerActor.props, StrategiesManagerActor.path)
  system.actorOf(PersistenceActor.props, PersistenceActor.path)
  system.actorOf(TradeRouteActor.props, TradeRouteActor.path)
  system.actorOf(DataManagerActor.props, DataManagerActor.path)

  // 启动REST 服务
  IO(Http) ! Http.Bind(httpServer, "127.0.0.1", port = 8888)

//  val ref = system.actorOf(SinaL1Actor.props())
//  val manager = system.actorOf(SecuritiesManagerActor.props(), SecuritiesManagerActor.Path)
//
//  ref ! new AskListenedSymbol("000001.XSHE")
//  ref ! new Execute()

  // TODO: 检测 服务是否启动完毕
  StdIn.readLine()
  system.shutdown()
}
