package quanter.rest

import akka.actor.ActorSelection
import org.json4s.{DefaultFormats, Extraction, Formats}
import spray.routing.HttpService
import org.json4s.jackson.JsonMethods._
import quanter.actors.strategy._
import akka.pattern.ask
import akka.util.Timeout
import quanter.actors._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

/**
  *
  */
trait StrategyService extends HttpService {
//  val strategiesManager = new StrategiesManager()
  val manager = actorRefFactory.actorSelection("/user/" + StrategiesManagerActor.path)
  val strategyServiceRoute = {
    get {
      path("strategy") {
        complete("""{"code":404}""")
      } ~
      path("strategy" / "list") {
        complete {
          _getAllStrategies()
        }
      } ~
      path("strategy" / "history" / IntNumber) {
        id =>
          complete {
            _readHistoryStrategy(id)
          }
      } ~
      path("strategy" / "real" / IntNumber) {
        id =>
          complete {
            "历史实盘"
          }
      } ~
      path("strategy" / IntNumber) {
        id =>
          complete {
            _getStrategy(id)
          }
      }~
      path("strategy"/ IntNumber / "portfolio") {
        id =>
          complete {
            """{"code":0, "portfolio":{"cash":10000, "frozen":0, "available":10000,"assets":300000, "positions":[{"symbol":"000001.XSHE","price":13, "cost": 10.3},{"symbol":"000002.XSHE","price":13, "cost": 9.3}]}}"""
          }
      }
    }  ~
    post {
      path("strategy" / "addrisk" / IntNumber / Segment / Segment) {
        (id, risk, rule) =>
            complete {
              _addRiskRule(id, risk, rule)
            }
      } ~
      path("strategy") {
        requestInstance {
          request =>
            complete {
              _createStrategy(request.entity.data.asString)
            }
        }
      }~
      path("strategy" / IntNumber / "order") {
        id =>
          requestInstance { request =>
            complete {
              _createOrder(id, request.entity.data.asString)
            }
          }
      }
    } ~
    put {
      path("strategy" / IntNumber) {
        id=>
        requestInstance {
          request =>
            complete {
              _updateStrategy(id, request.entity.data.asString)
            }
        }
      } ~
      path("strategy" / "start" / IntNumber) {
        id => {
          complete {
            _startStrategy(id)
          }
        }
      }~
      path("strategy" / "stop" / IntNumber) {
        id => {
          complete {
            _stopStrategy(id)
          }
        }
      }~
      path("strategy"/"enablerisk"/IntNumber) {
        id => {
          complete {
            _enableRiskControl(id)
          }
        }
      }~
      path("strategy"/"disablerisk"/IntNumber) {
        id => {
          complete {
            _disableRiskControl(id)
          }
        }
      }
    } ~
    delete {
      path("strategy" /IntNumber) {
        id =>
        complete {
          _deleteStrategy(id)
        }
      } ~
      path("strategy" / IntNumber / "order" / IntNumber) {
        (sid, oid) => complete {
          _cancelOrder(sid, oid)
        }
      }
    }
  }

  /**
    * 从数据库中读取所有的策略
    *
    * @return
    */
  private def _getAllStrategies(): String = {
    try {
      implicit val timeout = Timeout(5 seconds)
      val future = manager ? ListStrategies

      val result = Await.result(future, timeout.duration).asInstanceOf[Option[Array[Strategy]]]
      implicit val formats: Formats = DefaultFormats
      val retStrategyList = RetStrategyList(0, "成功", result)
      val json = Extraction.decompose(retStrategyList)
      compact(render(json))
    }  catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  /**
    * 创建一个策略
    *
    * @param json
    * @return
    */
  private def _createStrategy(json: String): String = {
    // 将JSON转换为strategy，加入到strategy
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val strategy = jv.extract[Strategy]

      manager ! NewStrategy(strategy)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }


  private def _getStrategy(id: Int): String = {
    try {
      implicit val timeout = Timeout(5 seconds)

      val strategyRef = _findStrategyActor(id)
      val future = strategyRef ? new GetStrategy(id)
      var  ret = ""
      val result = Await.result(future, timeout.duration).asInstanceOf[Option[Strategy]]
      if(result == None) ret = """{"code":1}"""
      else {
        implicit val formats: Formats = DefaultFormats
        val retStrategy = RetStrategy(0, "获取成功", result)
        val json = Extraction.decompose(retStrategy)
        ret = compact(render(json))
      }

      ret
    } catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _updateStrategy(id: Int, json: String): String = {
    implicit val formats = DefaultFormats
    try {
      val strategyRef = _findStrategyActor(id)
      val jv = parse(json)
      val strategy = jv.extract[Strategy]

      strategyRef ! new UpdateStrategy(strategy)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }

  }

  private def _deleteStrategy(id: Int): String = {
    try {
      //      strategiesManager.removeStrategy(id)
      manager ! DeleteStrategy(id)
      """{"code":0, "message":"成功删除"}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  /**
    * 启动策略
    *
    * @param id
    * @return
    */
  private def _startStrategy(id: Int): String = {
    try {
      val strategyRef = _findStrategyActor(id)
       strategyRef ! new StartStrategy(id)
      """{"code":0}"""
    } catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  /**
    * 停止策略
    *
    * @param id
    * @return
    */
  private def _stopStrategy(id: Int): String = {
    try {
      val strategyRef = _findStrategyActor(id)
      strategyRef ! new StopStrategy(id)
      """{"code":0}"""
    } catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }
  /**
    * 暂停策略运行
    *
    * @param id
    * @return
    */
  private def _pause(id: Int) : String = {
    try {
      val strategyRef = _findStrategyActor(id)
      strategyRef ! new PauseStrategy(id)
      """{"code":0}"""
    } catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _readHistoryStrategy(id: Int): String = {
    "历史信息，等待支持"
  }

  private def _addRiskRule(id: Int, risk: String, rule: String): String = {
    try {
      val strategyRef = _findStrategyActor(id)
      strategyRef ! new AddRisk(risk, rule)
      """{"code":0}"""
    } catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _findStrategyActor(id: Int): ActorSelection = actorRefFactory.actorSelection("/user/%s/%d".format(StrategiesManagerActor.path, id))

  private def _enableRiskControl(id: Int): String = {
    try {
      val strategyRef = _findStrategyActor(id)
      strategyRef ! new OpenRiskControl(id)
      """{"code":0}"""
    } catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _disableRiskControl(id: Int): String = {
    try {
      val strategyRef = _findStrategyActor(id)
      strategyRef ! new CloseRiskControl(id)
      """{"code":0}"""
    } catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _createOrder(id: Int, json: String): String = {
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val transaction = jv.extract[Transaction]

      val strategyRef = _findStrategyActor(id)
      strategyRef ! transaction
//      tradeRoute ! transaction
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _cancelOrder(sid: Int, oid: Int): String = {
    """{"code":0}"""
  }


}
