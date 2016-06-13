/**
  *
  */
package quanter.rest

import spray.routing.HttpService

/**
  * 提供一些非推送服务。
  */
trait DataService extends HttpService {

  val dataServiceRoute = _symbolsRoute ~ _financeDataRoute ~ _marketDataRoute

  private val _symbolsRoute = {
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
      }
    }
    }

  private val _financeDataRoute = {
    get {
      path("data" / "finance") {
        complete("")
      }
    }
  }

  private val _marketDataRoute = {
    get {
      path("data" / "market") {
        complete("")
      }
    }
  }
}
