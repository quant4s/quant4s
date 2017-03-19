/**
  *
  */
package org.quant4s.actors.persistence

import akka.actor.{Actor, Props}
import org.quant4s.actors._
import org.quant4s.persistence.{EPortfolio, EStrategy, StrategyDao}
import org.quant4s.rest._

import scala.collection.mutable.ArrayBuffer

/**
  * 保留， 后期使用
  */
class StrategyPersistorActor extends  BasePersistorActor{
  val strategyDao = new StrategyDao

  override def receive: Receive = {
    case action: NewStrategy => _saveStrategy(action.strategy)
    case action: DeleteStrategy => _deleteStrategy(action.id)
    case action: UpdateStrategy => _updateStrategy(action.strategy.id, action.strategy)
    case action: GetStrategy => _getStrategy(action.id)
    case action: ListStrategies => _listStrategies()
  }
  private def _saveStrategy(strategy: Strategy): Unit = {
    val s = EStrategy(strategy.id, strategy.name, strategy.runMode, strategy.status, strategy.lang.getOrElse("C#"))
    val s1 = strategyDao.insert(s)

    sender ! s1
  }

  private def _getStrategy(id: Int): Unit = {
    val strategy = strategyDao.getById(id)
    sender ! strategy
  }

  private def _updateStrategy(id: Int, strategy: Strategy): Unit = {
    val s = EStrategy(id, strategy.name, strategy.runMode, strategy.status, strategy.lang.getOrElse("C#"))
    val query = strategyDao.update(id, s)
  }

  private def _deleteStrategy(id: Int): Unit = {
    strategyDao.delete(id)
  }

  private def _listStrategies(): Unit = {
    log.debug("加载所有策略列表")
    val strategyArr = new ArrayBuffer[Strategy]()
    for(s <- strategyDao.list) {
      val strategy = Strategy(s.id, s.name, s.runMode, s.status, Some(s.lang), None)
      strategyArr += strategy
    }

    sender ! strategyArr.toArray
  }
}

object StrategyPersistorActor {
  def props = {
    Props(classOf[StrategyPersistorActor])
  }

  val path = "strategy_persistence"
}
