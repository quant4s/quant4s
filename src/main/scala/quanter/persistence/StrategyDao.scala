/**
  *
  */
package quanter.persistence

import scala.slick.driver.H2Driver.simple._

/**
  *
  */
class StrategyDao(implicit session: Session) extends BaseDao[EStrategy] {

  def getById(id: Int) = {
    val strategyQuery = strategies.filter(_.id === id)
  }

  override def update(id: Int, strategy: EStrategy): Unit = {
    val strategyQuery = strategies.filter(_.id === id)
    strategyQuery.map(s => (s.name, s.runMode, s.lang)).update(strategy.name, strategy.runMode, strategy.lang)
  }

  override  def insert(strategy: EStrategy): Unit = {
    strategies.map(s => (s.name, s.runMode, s.lang)) += (strategy.name, strategy.runMode, strategy.lang)
  }

  override  def delete(id: Int): Unit = {
    val strategyQuery = strategies.filter(_.id === id)
    strategyQuery.delete
  }

  override def list(): List[EStrategy] = {
    strategies.list
  }
}
