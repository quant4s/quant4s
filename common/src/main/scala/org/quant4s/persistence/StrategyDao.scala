/**
  *
  */
package org.quant4s.persistence

import profile.simple._
//import scala.slick.driver.H2Driver.simple._

/**
  *
  */
class StrategyDao(implicit session: Session) extends BaseDao[EStrategy] {

  def getById(id: Int): Option[EStrategy] = {
    val strategy1 = gStrategies.filter(_.id === id).take(1).firstOption
    if(!strategy1.isEmpty) {
      // 获取资金组合
      // val portfolio = portfolios.filter(_.strategyId === id).take(1).firstOption

      // 获取证券持仓

    }
    strategy1
  }

  override def update(id: Int, strategy: EStrategy) = {
    val strategyQuery = gStrategies.filter(_.id === id)
    strategyQuery.map(s => (s.name, s.runMode, s.lang)).update(strategy.name, strategy.runMode, strategy.lang)
  }

  override  def insert(strategy: EStrategy): EStrategy = {
//    val id = ( gStrategies returning gStrategies.map(_.id) += strategy) //(strategy.name, strategy.runMode, strategy.status, strategy.lang))

    gStrategies.map(s => (s.id, s.name, s.runMode, s.status, s.lang)) += (strategy.id, strategy.name, strategy.runMode, strategy.status, strategy.lang)
    // strategy.copy(id = Some(id))
    strategy
  }

  override  def delete(id: Int): Unit = {
    val strategyQuery = gStrategies.filter(_.id === id)
    strategyQuery.delete
  }

  override def list(): List[EStrategy] = {
    gStrategies.list
  }
}
