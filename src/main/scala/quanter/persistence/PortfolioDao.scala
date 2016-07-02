/**
  *
  */
package quanter.persistence
import scala.slick.driver.H2Driver.simple._
/**
  *
  */
class PortfolioDao(implicit session: Session)  extends BaseDao[EPortfolio] {
  override def getById(id: Int): Option[EPortfolio] = {
    portfolios.filter(_.strategyId === id).take(1).firstOption
  }

  override def update(id: Int, entity: EPortfolio): Unit = ???

  override def insert(entity: EPortfolio): Unit = ???

  override def delete(id: Int): Unit = ???

  override def list(): List[EPortfolio] = ???
}
