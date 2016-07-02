/**
  *
  */
package quanter.actors.strategies

import java.util.Date

import akka.actor.{Actor, Props}
import quanter.actors.persistence.{PersistenceActor, SaveStrategy}
import quanter.persistence.{EStrategy, StrategyDao}
import quanter.rest.{Portfolio, Strategy}
import quanter.strategies.{PortfolioCache, StrategyCache}

case class CreateStrategy(strategy: Strategy)
case class UpdateStrategy(strategy: Strategy)
case class DeleteStrategy(id: Int)
case class RunStrategy(id: Int)
case class ListStrategies()
case class GetStrategy(id: Int)

case class GetPortfolio(id: Int)

/**
  *
  */
class StrategiesManagerActor extends Actor{
  val strategyCache = new StrategyCache()
  val portfolioCache = new PortfolioCache()
  val persisRef = context.actorSelection("/user/" + PersistenceActor.path)

  override def receive: Receive = {
    case s: CreateStrategy => _saveStrategy(s.strategy)
    case s: UpdateStrategy => _updateStrategy(s.strategy)
    case s: DeleteStrategy => _deleteStrategy(s.id)
    case s: RunStrategy => _runStrategy(s.id)
    case s: ListStrategies => _listStrategies()
    case s: GetStrategy => _getStrategy(s.id)
  }

  private def _getStrategy(id: Int): Unit = {
   //  println ("长度为："+ managers.getAllStrategies().length)
    val strategy = strategyCache.getStrategy(id)
    val portfolio = Portfolio(10.0, new Date(), None)
    // strategy.get.portfolio = Some(portfolio)
    sender ! strategy
  }

  private def _saveStrategy(strategy: Strategy) = {
    // 保存到数据库
    val es = EStrategy(None, strategy.name, strategy.runMode, strategy.status, strategy.lang.getOrElse("C#"))
    persisRef ! SaveStrategy(es)

    strategyCache.addStrategy(strategy)
  }

  private def _listStrategies() = {
    sender ! strategyCache.getAllStrategies()
  }
  private def _updateStrategy(strategy: Strategy) = {
    // TODO: 保存到数据库
    strategyCache.modifyStrategy(strategy)
  }

  private def _deleteStrategy(id: Int) = {
    // TODO: 保存到数据库
    strategyCache.removeStrategy(id)
  }

  private def _runStrategy(id: Int): Unit = {
    val strategy = strategyCache.getStrategy(id)
    strategy.get.status = 1

    // TODO: 保存到数据库
  }

  private def _getPortfolio(id: Int): Unit = {

  }
}

object StrategiesManagerActor {
  def props = {
    Props(classOf[StrategiesManagerActor])
  }

  val path = "StrategiesManager"
}
