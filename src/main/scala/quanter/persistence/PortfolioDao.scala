/**
  *
  */
package quanter.persistence
import scala.slick.driver.H2Driver.simple._
/**
  *
  */
class PortfolioDao(session: Session)  extends BaseDao[EPortfolio] {
  override def getById(id: Int): Unit = ???

  override def update(id: Int, entity: EPortfolio): Unit = ???

  override def insert(entity: EPortfolio): Unit = ???

  override def delete(id: Int): Unit = ???

  override def list(): List[EPortfolio] = ???
}
