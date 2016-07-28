package quanter

import akka.actor.ActorSystem
import akka.io.IO
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import quanter.actors.data.DataManagerActor
import quanter.actors.persistence.PersistenceActor
import quanter.actors.receivers.SinaL1Actor
import quanter.actors.securities.SecuritiesManagerActor
import quanter.actors.strategies.{CreateStrategy, StrategiesManagerActor}
import quanter.actors.trade.{CreateTrader, TradeRouteActor}
import quanter.actors.ws.WebSocketActor
import quanter.rest.{HttpServer, Strategy, Trader}
import spray.can.Http

import scala.io.StdIn

/**
  * 启动入口
  */
object MainApp extends App {
  implicit val system = ActorSystem("server-system")

  val httpServer = system.actorOf(HttpServer.props)
  val manager = system.actorOf(SecuritiesManagerActor.props, SecuritiesManagerActor.path)
  val sinaL1Ref = system.actorOf(SinaL1Actor.props, SinaL1Actor.path)
  val strategyManagerRef = system.actorOf(StrategiesManagerActor.props, StrategiesManagerActor.path)
  val persistenceRef = system.actorOf(PersistenceActor.props, PersistenceActor.path)
  val tradeRouteRef = system.actorOf(TradeRouteActor.props, TradeRouteActor.path)
  val dataManagerRef = system.actorOf(DataManagerActor.props, DataManagerActor.path)
  val wsRef = system.actorOf(WebSocketActor.props, WebSocketActor.path)

  _createStrategy("""{"id": 911,"name": "带资金组合","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}""")
  _createStrategy("""{"id": 912,"name": "带资金组合","runMode":1, "status": 1, "portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}""")

  _createTrader("""{"id": 1002,"name": "SHSE","brokerType":"CTP", "brokerName":"THS", "brokerCode":"2011","brokerAccount":"66666660077","brokerPassword": "password", "brokerUri":"tcp://33.44.55.32:8099","status": 0}""")
  _createTrader("""{"id": 1001,"name": "SHSE","brokerType":"THS", "brokerName":"THS", "brokerCode":"2011","brokerAccount":"66666660077","brokerPassword": "password", "brokerUri":"tcp://33.44.55.32:8099","status": 0}""")
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

  private def _createStrategy(json: String): String = {
    // 将JSON转换为strategy，加入到strategy
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val strategy = jv.extract[Strategy]

      strategyManagerRef ! CreateStrategy(strategy)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _createTrader(json: String): String = {
    // 将JSON转换为strategy，加入到strategy
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val trader = jv.extract[Trader]

      tradeRouteRef ! CreateTrader(trader)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }
}
