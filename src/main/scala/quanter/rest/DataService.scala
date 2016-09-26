/**
  *
  */
package quanter.rest


import quanter.actors.AskListenedSymbol
import quanter.actors.data.{DataManagerActor, RequestBarData, RequestIndicatorData, RequestTickData}
import quanter.actors.provider.DataProviderManagerActor
import spray.routing.HttpService

/**
  * 提供一些非推送服务。
  */
trait DataService extends HttpService {
  val dataManager = actorRefFactory.actorSelection("/user/" + DataManagerActor.path)
  val providerManager = actorRefFactory.actorSelection("/user/" + DataProviderManagerActor.path)

  val dataServiceRoute = {
    post {
      path("data"/ Rest) { topic =>
        requestInstance { request =>
          complete {
            _subscribe(topic, request.entity.data.asString)
          }
        }
      }
    }
  }

  private def _subscribe(topic: String, subscription: String): String = {
    try {
      val arr = subscription.split(",")
      val symbol = arr(0)
      val _type = arr(1)

      providerManager ! AskListenedSymbol(symbol)

      _type match {
        case "TICK" => dataManager ! RequestTickData(topic, subscription)
        case "BAR" =>dataManager ! RequestBarData(topic, subscription)
        case _ =>dataManager ! RequestIndicatorData(subscription, subscription)
      }
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

}
