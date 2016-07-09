/**
  *
  */
package quanter.rest


import quanter.actors.data.{RequestBarData, RequestIndicatorData, RequestTickData, DataManagerActor}
import quanter.actors.strategies.{CreateStrategy, StrategiesManagerActor}
import spray.routing.HttpService

/**
  * 提供一些非推送服务。
  */
trait DataService extends HttpService {
  val dataManager = actorRefFactory.actorSelection("/user/" + DataManagerActor.path)

  val dataServiceRoute = {
    get {
      path("data" / "symbols") {
        complete("获取可交易的代码表")
      }~
      path("data" /"symbols" / "equity") {
        complete("股票列表")
      }~
      path("data" /"symbols" / "future") {
        complete("")
      }~
      path("data" /"symbols" / "option") {
        complete("")
      } ~
      path("data" /"symbols" / "spif") {
        complete("")
      } ~
      path("data" / "finance") {
        complete("")
      }~
      path("data" / "market") {
        complete("")
      }
    }~
    post {
      path("data"/ Rest) { subscription =>
        complete {
          _subscribe(subscription)
        }
      }
    }
  }

  private def _subscribe(subscription: String): String = {
    try {
      val arr = subscription.split(",")
      val symbol = arr(0)
      val _type = arr(1)

      _type match {
        case "TICK" => dataManager ! RequestTickData(subscription)
        case "BAR" =>dataManager ! RequestBarData(subscription)
        case _ =>dataManager ! RequestIndicatorData(subscription)
      }
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

}
