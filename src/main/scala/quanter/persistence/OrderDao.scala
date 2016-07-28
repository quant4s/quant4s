/**
  *
  */
package quanter.persistence

import scala.slick.driver.H2Driver.simple._

/**
  * 对订单的数据库处理
  */
class OrderDao (implicit session: Session)  extends BaseDao[EOrder] {
  override def getById(id: Int): Option[EOrder] = gOrders.filter(_.id === id).take(1).firstOption

  override def update(id: Int, entity: EOrder): Unit = {
    // nothing to do
  }

  override def insert(entity: EOrder): Unit = {
    // strategies.map(s => (s.name, s.runMode, s.lang)) += (entity.name, entity.runMode, entity.lang)
    gOrders.map(s => (s.orderNo, s.strategyId, s.symbol, s.orderType, s.side, s.transactTime, s.quantity, s.openClose, s.price, s.currency, s.securityExchange)) +=
      (entity.orderNo, entity.strategyId, entity.symbol, entity.orderType, entity.side, entity.transactTime, entity.quantity, entity.openClose, entity.price, entity.currency, entity.securityExchange)
  }

  override def delete(id: Int): Unit = {
    // nothing to do
  }

  override def list(): List[EOrder] = {
    gOrders.list
  }
}
