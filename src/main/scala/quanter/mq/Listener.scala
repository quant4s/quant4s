package quanter.mq

import akka.actor.Actor
import quanter.actors.data.{RequestBarData, RequestIndicatorData, RequestTickData, DataManagerActor}
import org.json4s._
import org.json4s.jackson.{Serialization, JsonMethods}

/**
  * 监听策略发送过来的数据
  * 1、初始化一个策略
  * init_strategy.json
  * 2、请求的数据（BAR|TICK|指标）
  *   data:[symbol:"000001.XSHE,BAR,5",symbol:"000001.XSHE,MACD,5,3|3|6",symbol:"000001.XSHE,TICK"]
  * 3、策略下单
  *  order.json
  */
class Listener extends Actor{
  val manager = context.actorSelection(DataManagerActor.path)

  override def receive: Receive = {
    case _ =>
  }

  private def _parseStrategy(json: String): Unit = {
    val s = JsonMethods.parse(json)

  }

  private def _parseSymbol(json: String): Unit = {
    val symbol = ""
    val arr = symbol.split(",")
    arr.length match {
      case 2 => // TICK
        _askTickData(symbol)
      case 3 => // BAR
        _askBarData(symbol)
      case 4 => // INDICATOR
        _askIndicatorData(symbol)
    }
  }

  // TODO: 需要根据登录信息比对，
  private def _askIndicatorData(json: String): Unit = {
    manager ! new RequestIndicatorData(json)
  }

  private def _askBarData(json: String): Unit = {
    manager ! new RequestBarData(json)
  }

  private def _askTickData(json: String): Unit = {
    manager ! new RequestTickData(json)
  }
}
