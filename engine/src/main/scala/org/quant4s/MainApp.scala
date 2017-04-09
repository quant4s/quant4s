package org.quant4s

import scala.io.StdIn
import akka.actor.ActorSystem
import akka.io.IO
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import org.quant4s.rest.{FlashServer, HttpServer, Strategy, TradeAccount}
import org.quant4s.actors.{NewStrategy, NewTrader}
import org.quant4s.mds.data.DataManagerActor
import org.quant4s.actors.persistence.{AccountPersistorActor, OrderPersistorActor, PersistenceActor, StrategyPersistorActor}
import org.quant4s.actors.scheduling.QuartzActor
import org.quant4s.actors.securitySelection.SIManagerActor
import org.quant4s.actors.strategy.StrategiesManagerActor
import org.quant4s.actors.trade.{InitTradeRoute, TradeRouteActor}
import org.quant4s.actors.zeromq.{ZeroMQReqRspServerActor, ZeroMQSubPubServerActor}
import org.quant4s.mds.SecuritiesManagerActor
import org.quant4s.mds.provider.DataProviderManagerActor
import spray.can.Http

/**
  * 启动入口
  */
object MainApp extends App {
  implicit val system = ActorSystem("server-system")

  // Http rest server
  val httpServer = system.actorOf(HttpServer.props, HttpServer.path)

  // 启动持久化层
  val persistenceRef = system.actorOf(PersistenceActor.props, PersistenceActor.path)
  val tradePersistenceRef = system.actorOf(AccountPersistorActor.props, AccountPersistorActor.path)
  val orderPersistenceRef = system.actorOf(OrderPersistorActor.props, OrderPersistorActor.path)
  val strategyPersistenceRef = system.actorOf(StrategyPersistorActor.props, StrategyPersistorActor.path)

  // 启动交易路由
  val tradeRouteRef = system.actorOf(TradeRouteActor.props, TradeRouteActor.path)
  system.actorOf(SIManagerActor.props, SIManagerActor.path)

  // 启动市场数据管理器
  val manager = system.actorOf(SecuritiesManagerActor.props, SecuritiesManagerActor.path)
  val dataProviderManagerRef = system.actorOf(DataProviderManagerActor.prop, DataProviderManagerActor.path)
  val dataManagerRef = system.actorOf(DataManagerActor.props, DataManagerActor.path)

  // 启动定时作业管理器
  val quartz = system.actorOf(QuartzActor.props, QuartzActor.path)

  // 启动策略管理器
  val strategyManagerRef = system.actorOf(StrategiesManagerActor.props, StrategiesManagerActor.path)

  // 启动ZMQ 服务器
  val pub = system.actorOf(ZeroMQSubPubServerActor.props, ZeroMQSubPubServerActor.path)
  val rsp = system.actorOf(ZeroMQReqRspServerActor.props, ZeroMQReqRspServerActor.path)

  //  _createStrategy("""{"id": 3,"name": "测试数据","runMode":1, "lang": "C#", "status": 1}""")
  //  _createStrategy("""{"id": 4,"name": "带资金组合","runMode":1, "lang": "PYTHON", "status": 1}""")

  _initTrader()
  //  _createTrader("""{"name": "SHSE","brokerType":"SIM", "brokerName":"仿真接口", "brokerCode":"2011","brokerAccount":"66666660077","brokerPassword": "password", "brokerUri":"tcp://33.44.55.32:8099","status": 0}""")
  //  _createTrader("""{"name": "SHSE","brokerType":"CTP", "brokerName":"快期", "brokerCode":"9999","brokerAccount":"071003","brokerPassword": "123456", "brokerUri":"tcp://180.168.146.187:10030","status": 0}""")

  // 启动REST 服务
  IO(Http) ! Http.Bind(httpServer, "127.0.0.1", port = 8888)

  // 启动 843 端口服务
  val flashServer = system.actorOf(FlashServer.props, FlashServer.path)
  //  IO(Http) ! Http.Bind(httpServer, "127.0.0.1", port = 843)


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
      val trader = jv.extract[TradeAccount]

      tradeRouteRef ! NewTrader(trader)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }
}
