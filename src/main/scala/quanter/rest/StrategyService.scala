package quanter.rest

import spray.routing._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import quanter.domain.Strategy

/**
  *
  */
trait StrategyService extends HttpService {

  val strategyServiceRoute = {
    get {
      path("strategy") {
        complete("暂不支持")
      } ~
        path("strategy" / "list") {
          complete {
            _readAllStrategies()
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
            complete("real strategy id: " + id)
        } ~
        path("strategy" / IntNumber) {
          id =>
            complete {
              _readStrategy(id)
            }
        }
    } ~
    post {
      path("strategy") {
        complete("创建策略")
      }
    } ~
    put {
      path("strategy") {
        complete("更新策略")
      }
    } ~
    delete {
      path("strategy") {
        complete("删除策略")
      }
    }
  }

  /**
    * 从数据库中读取所有的策略
    *
    * @return
    */
  private def _readAllStrategies(): String = {
    val strategies = List(new Strategy(1), new Strategy(2))

    val json =
      (strategies.map { w =>
        (("id" -> w.id) ~~ ("name" -> w.name))
      })

    compact(render(json))
  }

  private def _readStrategy(id: Int): String = {
    val strategy = new Strategy(id)

    val json = ("strategy" -> ("id" -> id) ~~ ("name" -> strategy.name) ~~ ("cash" -> 1000))

    compact(render(json))
  }

  private def _readHistoryStrategy(id: Int): String = {
    "历史信息"
  }

  private def _backtestReport(id: Int): String = {
    "回测报告"
  }
}
