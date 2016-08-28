/**
  *
  */
package quanter.persistence

import scala.slick.driver.MySQLDriver.simple._
/**
  *
  */
class TraderDao(implicit session: Session) extends BaseDao[ETrader] {
  override def getById(id: Int): Option[ETrader] = ???

  override def update(id: Int, entity: ETrader): Unit = ???

  override def insert(entity: ETrader): ETrader =  {
    val id = ( gTraders returning gTraders.map(_.id) += entity) //(strategy.name, strategy.runMode, strategy.status, strategy.lang))
    entity.copy(id = Some(id))
  }

  override def delete(id: Int): Unit = gTraders.filter(_.id === id).delete

  override def list(): List[ETrader] = gTraders.list
}
