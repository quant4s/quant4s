/**
  *
  */
package org.quant4s.rest

import org.quant4s.actors.{AskListenedSymbol, _}
import org.quant4s.mds.data.{RequestBarData, RequestIndicatorData, RequestTickData}
import spray.routing.HttpService
import spray.util.LoggingContext

/**
  * 提供一些非推送服务。
  */
trait DataService extends HttpService {
  val dataManager = actorRefFactory.actorSelection("/user/" + PATH_DATA_MANAGER_ACTOR)
  val providerManager = actorRefFactory.actorSelection("/user/" + PATH_DATA_PROVIDER_MANAGER_ACTOR)

  def dataServiceRoute(implicit log: LoggingContext)  = {
    post {
      path("data"/ Rest) { topic =>
        requestInstance { request =>
          complete {
            log.debug("接受到订阅数据指令" +  request.entity.data.asString)
            _subscribe(topic, request.entity.data.asString)
          }
        }
      }
    }~
    get {
      path ("instruments") {
        complete {
          """
            |{"status":{"code":0,"msg":"RspStatusCode_Ok"},
            |"data":[
              |{"symbol":"CFFEX.IC1508","sec_type":4,"exchange":"CFFEX","sec_id":"IC1508","sec_name":"IC1508","is_active":0},
              |{"symbol":"SHSE.000801","sec_type":3,"exchange":"SHSE","sec_id":"000801","sec_name":"资源80","is_active":0},
              |{"symbol":"SHSE.000802","sec_type":3,"exchange":"SHSE","sec_id":"000802","sec_name":"500沪市","is_active":1},
              |{"symbol":"SHSE.000803","sec_type":3,"exchange":"SHSE","sec_id":"000803","sec_name":"300波动","is_active":0},
              |{"symbol":"SHSE.000804","sec_type":3,"exchange":"SHSE","sec_id":"000804","sec_name":"500波动","is_active":0},
              |{"symbol":"SHSE.000805","sec_type":3,"exchange":"SHSE","sec_id":"000805","sec_name":"A股资源","is_active":1},
              |{"symbol":"SHSE.000806","sec_type":3,"exchange":"SHSE","sec_id":"000806","sec_name":"消费服务","is_active":1},
              |{"symbol":"SHSE.000807","sec_type":3,"exchange":"SHSE","sec_id":"000807","sec_name":"食品饮料","is_active":1},
              |{"symbol":"SHSE.000808","sec_type":3,"exchange":"SHSE","sec_id":"000808","sec_name":"医药生物","is_active":1},
              |{"symbol":"SHSE.000809","sec_type":3,"exchange":"SHSE","sec_id":"000809","sec_name":"细分农业","is_active":0},
              |{"symbol":"SHSE.000810","sec_type":3,"exchange":"SHSE","sec_id":"000810","sec_name":"细分能源","is_active":0},
              |{"symbol":"SHSE.000811","sec_type":3,"exchange":"SHSE","sec_id":"000811","sec_name":"细分有色","is_active":0},
              |{"symbol":"SHSE.600777","sec_type":1,"exchange":"SHSE","sec_id":"600777","sec_name":"新潮能源","is_active":1},
              |{"symbol":"SHSE.600778","sec_type":1,"exchange":"SHSE","sec_id":"600778","sec_name":"友好集团","is_active":1},
              |{"symbol":"SHSE.600779","sec_type":1,"exchange":"SHSE","sec_id":"600779","sec_name":"水井坊","is_active":1},
              |{"symbol":"SHSE.600780","sec_type":1,"exchange":"SHSE","sec_id":"600780","sec_name":"通宝能源","is_active":1},
              |{"symbol":"SHSE.600781","sec_type":1,"exchange":"SHSE","sec_id":"600781","sec_name":"辅仁药业","is_active":1}]
              |}
          """.stripMargin
        }
      } ~
      path("crossdomain.xml") {
        complete {
          log.debug("请求跨域文件")
          """
            |<cross-domain-policy>
            |  <allow-access-from domain="*" />
            |</cross-domain-policy>
          """.stripMargin
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
