/**
  *
  */
package quanter.persistence

import scala.slick.driver.MySQLDriver.simple._
/**
  *
  */
class TraderDao(implicit session: Session) extends BaseDao[ETrader] {
  override def getById(id: Int): Option[ETrader] = gTraders.filter(_.id === id).take(1).firstOption


  override def update(id: Int, trader: ETrader): Unit =  {
    val traderQuery = gTraders.filter(_.id === id)
    traderQuery.map(t => (t.name,t.brokerType, t.brokerName, t.brokerCode, t.brokerAccount, t.brokerUri, t.status))
      .update(trader.name,trader.brokerType, trader.brokerName, trader.brokerCode, trader.brokerAccount,  trader.brokerUri, trader.status)
//    (trader.name,trader.brokerType, trader.brokerName, trader.brokerCode, trader.brokerAccount, trader.brokerPassword, trader.brokerUri, trader.brokerServicePwd, trader.status)

  }

  override def insert(entity: ETrader): ETrader =  {
    val id = ( gTraders returning gTraders.map(_.id) += entity) //(strategy.name, strategy.runMode, strategy.status, strategy.lang))
    entity.copy(id = Some(id))
  }

  override def delete(id: Int): Unit = gTraders.filter(_.id === id).delete

  override def list(): List[ETrader] = gTraders.list
}
