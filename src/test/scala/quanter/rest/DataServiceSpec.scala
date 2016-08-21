/**
  *
  */
package quanter.rest

import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import spray.http.{HttpEntity, MediaTypes}

/**
  *
  */
class DataServiceSpec  extends RoutingSpec with DataService{
  implicit def actorRefFactory = system


  "数据管理," - {
    " 提交数据订阅请求" in {
      Post("/data/000001.XSHE,BAR,5") ~> dataServiceRoute ~> check {
        responseAs[String] === """{"code":0}"""
      }
      Post("/data/000001.XSHE,TICK") ~> dataServiceRoute ~> check {
        responseAs[String] === """{"code":0}"""
      }
      Post("/data/000001.XSHE,MACD,5,9|9|21") ~> dataServiceRoute ~> check {
        responseAs[String] === """{"code":0}"""
      }

    }

  }

}
