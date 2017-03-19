/**
  *
  */
package org.quant4s.persistence

import profile.simple._

/**
  * 成交记录
  */
class TransactionDao(implicit session: Session)  extends BaseDao[ETradeTransaction]{
  override def getById(id: Int): Option[ETradeTransaction] = gTradeTransactions.filter(_.id === id).take(1).firstOption

  override def update(id: Int, entity: ETradeTransaction): Unit = {}

  override def insert(entity: ETradeTransaction): ETradeTransaction = {
    val id = ( gTradeTransactions returning gTradeTransactions.map(_.id) += entity) //(strategy.name, strategy.runMode, strategy.status, strategy.lang))
    entity.copy(id = Some(id))
  }

  override def delete(id: Int): Unit = {}

  override def list(): List[ETradeTransaction] = gTradeTransactions.list
}
