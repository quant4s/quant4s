/**
  *
  */
package quanter.actors.securitySelection

import akka.actor.{Actor, ActorLogging}
import quanter.securitySelection.Selector

/**
  * 当接收到字符串的时候，进行分析 解释
  * PE GT 10 LT 15; 财务指标 送到 FinanceIndiActor 进行计算
  */
class SelectionInterpreterActor extends Actor with ActorLogging{
  var indiCount = 0
  var resultCount = 0
  var result: Selector = null
  val financeIndiRef = context.actorSelection("/user")
  override def receive: Receive = {
    case s: String => { //接收到字符串以后，开始并发执行， TODO:  注意此处没有排序处理
      val arr = s.split(";")
      indiCount = arr.length
      arr.foreach( s1 => _parse(s1))
    }

    case r: Selector => {
      // 接收到结果
      resultCount += 1
      if(result == null) result = r
      else result = result.intersect(r)

      //if(resultCount == indiCount) // TODO:推送计算结果

    }
  }

  def _parse(s: String): Unit = {
    val arr = s.split(" ")
    val op = arr(1)
    val value = arr(2).toDouble
    arr(0) match {
      case "PE" => financeIndiRef ! new PE(op, value)
      case "PB" => financeIndiRef ! new PB(op, value)
      case "ROE" => financeIndiRef ! new ROE(op, value)
      case _ => log.warning("不支持的消息")
    }
  }
}





