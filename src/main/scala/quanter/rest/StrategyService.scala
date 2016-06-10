package quanter.rest

import java.text.SimpleDateFormat
import javax.xml.soap.SOAPMessage

import org.json4s.{DefaultFormats, Extraction, Formats}
import spray.routing._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import quanter.actors.strategies._
import quanter.strategies.StrategiesManager

/**
  *
  */
trait StrategyService extends HttpService {
  val strategiesManager = new StrategiesManager()
  val manager = actorRefFactory.actorSelection(StrategiesManagerActor.path)

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
      path("strategy" / "backtest" / IntNumber) {
        id =>
          complete {
            _backtestReport(id)
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
      }
    } ~
    post {
      path("strategy") {
        requestInstance {
          request =>
            complete {
              _createStrategy(request.entity.data.asString)
            }
        }
      }~
      path("strategy" / "run" / IntNumber) {
        id => {
          complete {
            _runStrategy(id)
          }
        }
      }
    } ~
    put {
      path("strategy") {
        requestInstance {
          request =>
            complete {
              _updateStrategy(request.entity.data.asString)
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
      }
    }
  }

  /**
    * 从数据库中读取所有的策略
    *
    * @return
    */
  private def _getAllStrategies(): String = {
    val strategies = strategiesManager.getAllStrategies()

    // TODO: 将strategyies 转换为 json
    """[{"id":1,"name":"unnamed"},{"id":2,"name":"unnamed"}]"""
  }

  private def _getStrategy(id: Int): String = {
    try {
      val strategy = strategiesManager.getStrategy(id)
      implicit val formats: Formats = DefaultFormats
      // 将strategy转换为json
      val json = Extraction.decompose(strategy)
      compact(render(json))
    } catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _runStrategy(id: Int): String = {
    try {
      manager ! RunStrategy(id)
      """{"code":0}"""
    } catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }
  private def _readHistoryStrategy(id: Int): String = {
    "历史信息，等待支持"
  }

  private def _backtestReport(id: Int): String = {
    "回测报告，等待支持"
  }

  private def _createStrategy(json: String): String = {
    // 将JSON转换为strategy，加入到strategy
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val strategy = jv.extract[Strategy]

//      strategiesManager.addStrategy(strategy)
      manager ! CreateStrategy(strategy)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _updateStrategy(json: String): String = {
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val strategy = jv.extract[Strategy]

//      strategiesManager.modifyStrategy(strategy)
      manager ! UpdateStrategy(strategy)
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
}
