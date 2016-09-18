/**
  *
  */
package quanter.actors.strategy

import java.util.Date

import akka.actor.{Actor, Props}
import quanter.actors.{NewStrategy, UpdateStrategy, GetStrategy, DeleteStrategy, ListStrategies}
import quanter.actors.persistence.PersistenceActor
import quanter.rest.{CancelOrder, Portfolio, Strategy}
import quanter.strategies.StrategyCache

case class UpdatePortfolio(strategy: Strategy)


/**
  * 1、策略的CRUD 操作
  * 2、买入|卖出股票
  * 3、
  */
class StrategiesManagerActor extends Actor{
  val strategyCache = new StrategyCache()
//  val portfolioCache = new Portfolio()
  val persisRef = context.actorSelection("/user/" + PersistenceActor.path)

  override def receive: Receive = {
    case s: NewStrategy => _createStrategy(s.strategy)
    case s: DeleteStrategy => _deleteStrategy(s.id)
    case ListStrategies => _listStrategies()
  }

  private def _createStrategy(strategy: Strategy) = {
    // 保存到数据库
//    val es = EStrategy(None, strategy.name, strategy.runMode, strategy.status, strategy.lang.getOrElse("C#"))
    context.actorOf(StrategyActor.props(strategy.id), strategy.id.toString)
//    strategyCache.addStrategy(strategy)
    persisRef ! new NewStrategy(strategy)
  }

  private def _listStrategies() = {
    sender ! Some(strategyCache.getAllStrategies())
    // persisRef ? ListStrategies
  }


  private def _deleteStrategy(id: Int) = {
    // TODO: 处理cache
    strategyCache.removeStrategy(id)
    persisRef ! DeleteStrategy(id)
  }

  private def _runStrategy(id: Int): Unit = {
    val strategy = strategyCache.getStrategy(id)
    strategy.get.status = 1

    // TODO: 保存到数据库
  }

  private def _getPortfolio(id: Int): Unit = {

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
