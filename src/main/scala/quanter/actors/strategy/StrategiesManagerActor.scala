/**
  *
  */
package quanter.actors.strategy

import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout
import quanter.actors._
import quanter.actors.persistence.PersistenceActor
import quanter.rest.{CancelOrder, HttpServer, Strategy, Trader}
import quanter.strategies.StrategyCache

import scala.concurrent.Await
import scala.concurrent.duration._
case class UpdatePortfolio(strategy: Strategy)


/**
  * 1、策略的CRUD 操作
  * 2、买入|卖出股票
  * 3、
  */
class StrategiesManagerActor extends Actor{
//  val strategyCache = new StrategyCache()
//  val portfolioCache = new Portfolio()
  val persisRef = context.actorSelection("/user/" + PersistenceActor.path)
  val restRef = context.actorSelection("/user/" + HttpServer.path)

  override def receive: Receive = {
    case s: NewStrategy => _createStrategy(s.strategy)
    case s: DeleteStrategy => persisRef ! DeleteStrategy(s.id)
    case s: ListStrategies => persisRef ! new ListStrategies()

    // 持久化层返回的一步消息
    case s: Array[Strategy] => restRef ! s
    case s: Strategy => restRef ! s
  }

  private def _createStrategy(strategy: Strategy) = {
    context.actorOf(StrategyActor.props(strategy.id), strategy.id.toString)
    persisRef ! new NewStrategy(strategy)
  }

  private def _cancelOrder(order: CancelOrder): Unit = {
    // 从持久层获取策略信息

    // TODO: 检查是否可以提交委托请求
    // TODO: 1.是否已经全部成交
    // TODO: 2.是否已经提交了撤单请求

    // 提交取消订单到交易路由， 交易路由根据账户返回结果修改状态
    // 提交新
  }
}

object StrategiesManagerActor {
  def props = Props(classOf[StrategiesManagerActor])

  val path = "StrategiesManager"
}
