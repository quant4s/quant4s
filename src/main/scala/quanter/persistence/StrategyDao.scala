/**
  *
  */
package quanter.persistence

import scala.slick.driver.H2Driver.simple._

/**
  *
  */
class StrategyDao(implicit session: Session) extends BaseDao[EStrategy] {

  def getById(id: Int): Option[EStrategy] = {
    val strategy1 = strategies.filter(_.id === id).take(1).firstOption
    if(!strategy1.isEmpty) {
      // 获取资金组合
      // val portfolio = portfolios.filter(_.strategyId === id).take(1).firstOption

      // 获取证券持仓

    }
    strategy1

    //strategyQuery.flatMap(_.po)
  }

  override def update(id: Int, strategy: EStrategy) = {
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
