package org.quant4s.rest

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import org.quant4s.actors._
import org.quant4s.actors.SecuritySelection
import spray.routing.HttpService
import spray.util.LoggingContext


/**
  *
  */
trait PickerService extends HttpService {

  val simRef = actorRefFactory.actorSelection("/user/" + PATH_SELECTION_INTERPRETER_MANAGER_ACTOR)
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
