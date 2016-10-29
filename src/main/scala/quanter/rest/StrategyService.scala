package quanter.rest

import akka.actor.Actor._
import akka.actor.ActorSelection
import akka.event.LoggingAdapter
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Extraction, Formats}
import quanter.actors._
import quanter.actors.strategy._
import spray.routing.HttpService
import spray.util.LoggingContext

import scala.collection.mutable

/**
  *
  */
trait StrategyService extends HttpService {
//  val strategiesManager = new StrategiesManager()
//  implicit def _log: LoggingAdapter
  val manager = actorRefFactory.actorSelection("/user/" + StrategiesManagerActor.path)
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
      path("strategy" / "start" / IntNumber) {
        id => {
          complete {
            val strategyRef = _findStrategyActor(id)
            strategyRef ! new StartStrategy(id)
            """{"code":0}"""
          }
        }
      }~
      path("strategy" / "stop" / IntNumber) {     // 停止一个策略
        id => {
          complete {
            val strategyRef = _findStrategyActor(id)
            strategyRef ! new StopStrategy(id)
            """{"code":0}"""
          }
        }
      }~
      path("strategy"/"enablerisk"/IntNumber) { // 允许风控
        id => {
          complete {
            val strategyRef = _findStrategyActor(id)
            strategyRef ! new OpenRiskControl(id)
            """{"code":0}"""
          }
        }
      }~
      path("strategy"/"disablerisk"/IntNumber) { // 净值风控
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
      path("strategy" /IntNumber) {   // 删除一个策略
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

  private def _findStrategyActor(id: Int): ActorSelection = actorRefFactory.actorSelection("/user/%s/%d".format(StrategiesManagerActor.path, id))

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
