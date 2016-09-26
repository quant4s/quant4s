/**
  *
  */
package quanter.actors.securitySelection

import akka.actor.{Actor, ActorLogging, Props}
import akka.routing.{RoundRobinPool, RoundRobinRouter}
import quanter.actors.zeromq.{PublishData, ZeroMQServerActor}
import quanter.rest.{FinanceIndi, SecurityPicker}
import quanter.securitySelection.Selector

/**
  * 当接收到字符串的时候，进行分析 解释
  * PE GT 10 LT 15; 财务指标 送到 FinanceIndiActor 进行计算
  */
class SelectionInterpreterActor(cmds: SecurityPicker, topic: String, selector: Selector) extends Actor with ActorLogging{
  var indiCount = cmds.financeIndi.length
  var resultCount = 0
  var result: Selector = selector
  val financeIndiRef = context.actorSelection("/user")
  val pubRef = context.actorSelection("/user/" + ZeroMQServerActor.path)
  val finIndiRouter = context.actorOf(RoundRobinPool(5).props(Props.create(classOf[FinanceIndiActor],selector)))

  _parse()

  override def receive: Receive = {
    case r: Selector => {
      // 接收到结果
      resultCount += 1
      result = result.intersect(r)

      if(resultCount == indiCount) {
        // TODO:推送计算结果
        var rs = ""
        result.pool.foreach( ins => rs = rs + ins.code + ",")
        if(rs .length > 0)
          rs = rs.substring(0, -1)
        pubRef ! PublishData(topic, rs)
      }
    }
  }

  def _parse(): Unit = {
    for(cmd <- cmds.financeIndi) { // TODO: 改成采用 router， 提高并发性
      finIndiRouter ! cmd
    }
  }
}

object SelectionInterpreterActor {
  def props(cmds: SecurityPicker, topic: String, selector: Selector) = {
    Props(classOf[SelectionInterpreterActor], cmds, topic, selector)
  }
}





