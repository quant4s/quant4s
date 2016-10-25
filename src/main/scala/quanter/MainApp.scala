package quanter

import scala.io.StdIn
import akka.actor.ActorSystem
import akka.io.IO
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import quanter.actors.{NewStrategy, NewTrader}
import quanter.actors.data.DataManagerActor
import quanter.actors.persistence.PersistenceActor
import quanter.actors.provider.DataProviderManagerActor
import quanter.actors.provider.sina.SinaL1Actor
import quanter.actors.scheduling.QuartzActor
import quanter.actors.securities.SecuritiesManagerActor
import quanter.actors.securitySelection.SIManagerActor
import quanter.actors.strategy.StrategiesManagerActor
import quanter.actors.trade.{InitTradeRoute, TradeRouteActor}
import quanter.actors.zeromq.ZeroMQServerActor
import quanter.rest.{HttpServer, Strategy, Trader}
import spray.can.Http

/**
  * 启动入口
  */
object MainApp extends App {
  implicit val system = ActorSystem("server-system")

  // MARKET actor
  val httpServer = system.actorOf(HttpServer.props, HttpServer.path)
  val persistenceRef = system.actorOf(PersistenceActor.props, PersistenceActor.path)
  val tradeRouteRef = system.actorOf(TradeRouteActor.props, TradeRouteActor.path)
  system.actorOf(SIManagerActor.props, SIManagerActor.path)

  val manager = system.actorOf(SecuritiesManagerActor.props, SecuritiesManagerActor.path)
  val quartz = system.actorOf(QuartzActor.props, QuartzActor.path)

  val strategyManagerRef = system.actorOf(StrategiesManagerActor.props, StrategiesManagerActor.path)

  // val sinaL1Ref = system.actorOf(SinaL1Actor.props, SinaL1Actor.path)
  val dataProviderManagerRef = system.actorOf(DataProviderManagerActor.prop, DataProviderManagerActor.path)
  val dataManagerRef = system.actorOf(DataManagerActor.props, DataManagerActor.path)
//  val wsRef = system.actorOf(WebSocketActor.props, WebSocketActor.path)

  //
  val pub = system.actorOf(ZeroMQServerActor.props, ZeroMQServerActor.path)

//  _createStrategy("""{"id": 1,"name": "测试数据","runMode":1, "lang": "C#", "status": 1, "portfolio": {"cash":120000, "date":"2004-09-04T18:06:22Z"}}""")
//  _createStrategy("""{"id": 2,"name": "带资金组合","runMode":1, "lang": "C#", "status": 1，"portfolio": {"cash":100000, "date":"2004-09-04T18:06:22Z"}}""")

  _initTrader()
//  _createTrader("""{"name": "SHSE","brokerType":"SIM", "brokerName":"仿真接口", "brokerCode":"2011","brokerAccount":"66666660077","brokerPassword": "password", "brokerUri":"tcp://33.44.55.32:8099","status": 0}""")
//  _createTrader("""{"name": "SHSE","brokerType":"CTP", "brokerName":"THS", "brokerCode":"2011","brokerAccount":"66666660077","brokerPassword": "password", "brokerUri":"tcp://33.44.55.32:8099","status": 0}""")

  // 启动REST 服务
  IO(Http) ! Http.Bind(httpServer, "127.0.0.1", port = 8888)


  // TODO: 检测 服务是否启动完毕
  StdIn.readLine()
  system.shutdown()

  private def _createStrategy(json: String): String = {
    // 将JSON转换为strategy，加入到strategy
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val strategy = jv.extract[Strategy]

      strategyManagerRef ! new NewStrategy(strategy)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _initTrader(): Unit = {
    tradeRouteRef ! new InitTradeRoute()
  }
  private def _createTrader(json: String): String = {
    // 将JSON转换为strategy，加入到strategy
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val trader = jv.extract[Trader]

      tradeRouteRef ! NewTrader(trader)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }
}
