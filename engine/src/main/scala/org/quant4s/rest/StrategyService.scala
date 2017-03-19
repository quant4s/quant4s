package org.quant4s.rest

import akka.actor.Actor._
import akka.actor.ActorSelection
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Extraction, Formats}
import org.quant4s.actors._
import spray.routing.HttpService
import spray.util.LoggingContext

import scala.collection.mutable

/**
  *
  */
trait StrategyService extends HttpService {
//  val manager = actorRefFactory.actorSelection("/user/" + StrategiesManagerActor.path)
  // TODO:
  val manager = actorRefFactory.actorSelection("/user/StrategiesManager")
  var strategyCache = new mutable.HashMap[Int, Strategy]()

  manager ! new ListStrategies()
  def strategyServiceRoute(implicit log: LoggingContext) = {
    get {
      path("strategy" / "list") {   // 获取策略列表
        complete {
          implicit val formats = DefaultFormats
          val result = if(strategyCache.size == 0) None else Some(strategyCache.values.toArray)
          val retStrategyList = RetStrategyList(0, "成功", result)
          val json = Extraction.decompose(retStrategyList)
          compact(render(json))
        }
      } ~
      path("strategy" / IntNumber) {  // 获取指定策略
        id =>
          complete {
            val result = strategyCache.get(id)
            if(result == None)  """{"code":1}"""
            else {
              implicit val formats: Formats = DefaultFormats
              val retStrategy = RetStrategy(0, "获取成功", result)
              val json = Extraction.decompose(retStrategy)
              compact(render(json))
            }
          }
      }~
      path("strategy" / IntNumber / "position") { // 获取持仓
        id =>
          complete {
              """ {"code":0,"message":"成功","data":[{
"exchange": "SHSE",
"sec_id": "000002",
"sec_name": "万科A",
"side": 1,
"volume": 30000,
"volume_today": 3000,
"price": 25.7,
"vwap": 24.4,
"fpnl": 3000,
"transact_time": 1477535817429
}]}"""
          }
      }~
      path("strategy" / IntNumber / "trans" / IntNumber) { // 获取最近成交
        (id, n) =>
          complete {
            """ {"code":0,"message":"成功","data":[{
"sec_id": "000002",
"sec_name": "万科A",
"side": 1,
"position_effect": 1,
"volume": 30000,
"price": 25.7,
"amount": 2000,
"transact_time": 1477535817429
}]}"""
          }
      }~
      path("strategy" / IntNumber / "order" / IntNumber) { // 获取订单
        (id, n) =>
          complete {
            """ {"code":0,"message":"成功","data":[{
"sec_id": "000002",
"sec_name": "万科A",
"side": 1,
"position_effect": 1,
"price": 25.7,
"order_type": 0,
"status": 2,
"volume":2000,
"filled_volume":355,
"filled_vwap":533,
"ord_rej_reason_detail": "-",
"sending_time": 1477535817429
}]}"""
          }
      }~
      path("strategy" / IntNumber / "cash") { // 获取现金
        id =>
          complete {
  """ {"code":0,"message":"成功","data":[{
"cash": 10000,
},{
"cash": 200000,
}]}"""
          }
      }~
      path("strategy" / IntNumber / "indicators") {
        id =>
          complete {
            """
              |{"status":{"code":0,"msg":"RspStatusCode_Ok"},"data":[{"strategy_id":"3"
              |,"nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count"
              |:0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit"
              |:0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value"
              |:0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":1477535817429}]}
            """.stripMargin
          }
      }~
      path("strategy" / IntNumber / "last_n_dailyindicators" / IntNumber) {
        (id, n) =>
          complete {
            """
              |{"status":{"code":0,"msg":"RspStatusCode_Ok"},"data":[{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14792859310738826},{"strategy_id":"a8044da1-2d5-11e5-9283-000c29804e2a","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14791121299480444},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14790497785728484},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":1478955592192071},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14788603793071272},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14787817673247944},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14742101221469696},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":1474069499994286},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14697532593705106},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":1469608991968276},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14695469722154984},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14694385751431664},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14684851122132796},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14683112149984056},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":1468048500000663},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14678075056966068},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14673597551093452},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14673288778590848},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14672051905552242},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":1467121412943456},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":146555483499611},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14653853877487824},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14651973000008092},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14648774642878942},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14639935589311048},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":1463825578793749},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":1463216254948572},{"strategy_id":"3","nav":0,"pnl":0,"profit_ratio":0,"sharp_ratio":0,"risk_ratio":0,"trade_count":0,"win_count":0,"lose_count":0,"win_ratio":0,"max_profit":0,"min_profit":0,"max_single_trade_profit":0,"min_single_trade_profit":0,"daily_max_single_trade_profit":0,"daily_min_single_trade_profit":0,"max_position_value":0,"min_position_value":0,"max_drawdown":0,"daily_pnl":0,"daily_return":0,"annual_return":0,"transact_time":14568165000005}]}
            """.stripMargin
          }
      }~
      path("strategy" / IntNumber / "unfinished_orders") {
        id =>
          complete {
            """ {"code":0,"message":"成功","data":[{
"sec_id": "000002",
"sec_name": "万科A",
"side": 1,
"position_effect": 1,
"price": 25.7,
"order_type": 0,
"status": 2,
"volume":2000,
"filled_volume":355,
"filled_vwap":533,
"ord_rej_reason_detail": "-",
"sending_time": 1477535817429
}]}""".stripMargin
          }
      }
    }  ~
    post {
      path("strategy" / "addrisk" / IntNumber / Segment / Segment) { // 增加一个风控规则
        (id, risk, rule) =>
            complete {
              val strategyRef = _findStrategyActor(id)
              strategyRef ! new AddRisk(risk, rule)
              """{"code":0}"""
            }
      } ~
      path("strategy") {  // 创建一个新的策略
        requestInstance {
          request =>
            complete {
              try {
               implicit val formats = DefaultFormats
                val jv = parse(request.entity.data.asString)
                val strategy = jv.extract[Strategy]
                manager ! NewStrategy(strategy)
                """{"code":0}"""
              }catch {
                case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
              }
            }
        }
      }~
      path("strategy" / IntNumber / "order") {  // 接受一个订单
        id =>
          requestInstance { request =>
            complete {
              try {
                implicit val formats = DefaultFormats
                val jv = parse(request.entity.data.asString)
                val transaction = jv.extract[Transaction]

                val strategyRef = _findStrategyActor(id)
                strategyRef ! transaction
                """{"code":0}"""
              }catch {
                case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
              }

            }
          }
      }
    } ~
    put {
      path("strategy" / IntNumber) { // 更新一个策略
        id=>
        requestInstance {
          request =>
            complete {
              try {
                implicit val formats = DefaultFormats
                val strategyRef = _findStrategyActor(id)
                val jv = parse(request.entity.data.asString)
                val strategy = jv.extract[Strategy]

                strategyRef ! new UpdateStrategy(strategy)
                """{"code":0}"""
              }catch {
                case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
              }
            }
        }
      } ~
      path("strategy" / IntNumber / "position") {
        id => {
          requestInstance {
            request =>
              complete { // 更新持仓
                log.debug("接收到持仓信息")
                ""
              }
          }
        }
      }~
      path("strategy" / IntNumber / "start") {
        id => {
          complete {
            val strategyRef = _findStrategyActor(id)
            strategyRef ! new StartStrategy(id)
            """{"code":0}"""
          }
        }
      }~
      path("strategy" / IntNumber / "stop") {     // 停止一个策略
        id => {
          complete {
            val strategyRef = _findStrategyActor(id)
            strategyRef ! new StopStrategy(id)
            """{"code":0}"""
          }
        }
      }~
      path("strategy" / IntNumber / "enablerisk") { // 允许风控
        id => {
          complete {
            val strategyRef = _findStrategyActor(id)
            strategyRef ! new OpenRiskControl(id)
            """{"code":0}"""
          }
        }
      }~
      path("strategy" / IntNumber / "disablerisk") { // 净值风控
        id => {
          complete {
            val strategyRef = _findStrategyActor(id)
            strategyRef ! new CloseRiskControl(id)
            """{"code":0}"""
          }
        }
      }
    } ~
    delete {
      path("strategy" / IntNumber) {   // 删除一个策略
        id =>
        complete {
          strategyCache.remove(id)
          manager ! DeleteStrategy(id)
          """{"code":0, "message":"成功删除"}"""
        }
      } ~
      path("strategy" / IntNumber / "order" / IntNumber) { // 撤单
        (sid, oid) => complete {
          """{"code":0}"""
        }
      }
    }
  }

//  private def _findStrategyActor(id: Int): ActorSelection = actorRefFactory.actorSelection("/user//%d".format(StrategiesManagerActor.path, id))
  private def _findStrategyActor(id: Int): ActorSelection = actorRefFactory.actorSelection("/user//%d".format("StrategiesManager", id))

  def respStrategyReceive(implicit log: LoggingContext): Receive = {
    // 策略缓存处理
    // 1. 构建策略缓存 2. 更新策略 3. 更新资金组合
    case t: Array[Strategy] => {
      _buildStrategyCache(t)
    }
    case t: Strategy => {
      _updateStrategyCache(t)
    }
  }

  private def _buildStrategyCache(strategies: Array[Strategy])(implicit log: LoggingContext) : Unit = {
    log.debug("在Rest Service层创建策略缓存")
    for(s <- strategies)
      strategyCache += (s.id -> s)
  }

  private def _updateStrategyCache(s: Strategy)(implicit log: LoggingContext) : Unit = {
    log.debug("在Rest Service层更新策略缓存")
    strategyCache(s.id) = s
  }

}
