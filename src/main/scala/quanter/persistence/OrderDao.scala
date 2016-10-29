/**
  *
  */
package quanter.persistence

import profile.simple._

//import scala.slick.driver.H2Driver.simple._

/**
  * 对订单的数据库处理
  */
class OrderDao (implicit session: Session)  extends BaseDao[EOrder]{
  override def getById(id: Int): Option[EOrder] = gOrders.filter(_.id === id).take(1).firstOption

  override def update(id: Int, entity: EOrder): Unit = {
    // nothing to do
  }

  override def insert(entity: EOrder): EOrder = {
    val id = ( gOrders returning gOrders.map(_.id) += entity) //(strategy.name, strategy.runMode, strategy.status, strategy.lang))
    entity.copy(id = Some(id))
  }

  override def delete(id: Int): Unit = {
    // nothing to do, 修改委托单的状态,
  }

  override def list(): List[EOrder] = {
    gOrders.list
  }
}
