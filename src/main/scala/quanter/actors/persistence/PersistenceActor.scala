/**
  *
  */
package quanter.actors.persistence

import akka.actor.{Actor, Props}
import quanter.persistence.EStrategy
import quanter.persistence._

import scala.slick.driver.H2Driver.simple._


/**
  *
  */

case class SaveStrategy(obj: EStrategy)
case class UpdateStrategy(id: Int, obj: EStrategy)
case class DeleteStrategy(id: Int)
case class GetStrategy(id: Int)
case class ListStrategies()

object PersistenceActor {
  def props = {
    Props(classOf[PersistenceActor])
  }

  def path = "persistence"
}
class PersistenceActor extends Actor {
  val dbUrl = "jdbc:h2:mem:test"
  val jdbcDriver = "org.h2.Driver"
  val user = "root"
  val password = ""
  val db = Database.forURL(dbUrl, user, password, driver = jdbcDriver)
  implicit val session = db.createSession()

  val ddl = strategies.ddl ++ portfolios.ddl ++ stockHoldings.ddl ++ transactions.ddl ++ orders.ddl ++ traders.ddl
  ddl.create

  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    session.close()
    super.postStop()
  }

  override def receive: Receive = {
    case action: SaveStrategy => _saveStrategy(action.obj)
    case action: DeleteStrategy => _deleteStrategy(action.id)
    case action: UpdateStrategy => _updateStrategy(action.id, action.obj)
    case action: GetStrategy => _getStrategy(action.id)
    case actoin: ListStrategies => _listStrategies()
    case _ =>
  }

  val dao = new StrategyDao
  private def _saveStrategy(strategy: EStrategy): Unit = {
    dao.insert(strategy)
  }

  private def _getStrategy(id: Int): Unit = {
    val strategy = dao.getById(id)
    sender ! strategy
  }

  private def _updateStrategy(id: Int, strategy: EStrategy): Unit = {
    dao.update(id, strategy)
  }

  private def _deleteStrategy(id: Int): Unit = {
    dao.delete(id)
  }

  private def _listStrategies(): Unit = {
    sender ! dao.list()
  }

}
