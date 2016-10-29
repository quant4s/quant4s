package quanter.rest

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import quanter.actors.SecuritySelection
import quanter.actors.securitySelection.SIManagerActor
import spray.routing.HttpService
import spray.util.LoggingContext

/**
  *
  */
trait PickerService extends HttpService {

  val simRef = actorRefFactory.actorSelection("/user/" + SIManagerActor.path)
  def pickerServiceRoute(implicit log: LoggingContext)  = {
    post {
      path("picker" / Rest) { topic =>
        requestInstance { request =>
          complete {
            _subscribe(topic, request.entity.data.asString)
          }
        }
      }
    }
  }

  def _subscribe(topic: String, json: String) = {
    // 分析JSON
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val cmds = jv.extract[SecurityPicker]
      simRef ! new SecuritySelection(topic, cmds)
      ""
    } catch {
      case ex: Exception =>
        """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }
}
