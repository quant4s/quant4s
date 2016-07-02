/**
  *
  */
package quanter.actors.persistence

import akka.actor.{Actor, Props}
import quanter.persistence.EStrategy
import quanter.persistence._
import quanter.rest.Portfolio

import scala.slick.driver.H2Driver.simple._


/**
  *
  */

case class SaveStrategy(obj: EStrategy)
case class UpdateStrategy(id: Int, obj: EStrategy)
case class DeleteStrategy(id: Int)
case class GetStrategy(id: Int)
case class ListStrategies()

case class SavePortfolio(obj: EPortfolio)
case class UpdatePortfolio(id: Int, obj: EPortfolio)
case class GetPortfolio(id: Int)

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

  val ddl = strategies.ddl ++ portfolios.ddl ++ stockHoldings.ddl ++ transactions.ddl ++ gOrders.ddl ++ traders.ddl
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

    case action: SavePortfolio =>
    case action: UpdatePortfolio =>
    case action: GetPortfolio =>
    case _ =>
  }

  val strategyDao = new StrategyDao
  val portfolioDao = new PortfolioDao()
  private def _saveStrategy(strategy: EStrategy): Unit = {
    strategyDao.insert(strategy)
  }

  private def _getStrategy(id: Int): Unit = {
    val strategy = strategyDao.getById(id)

    sender ! strategy
  }

  private def _updateStrategy(id: Int, strategy: EStrategy): Unit = {
    val query = strategyDao.update(id, strategy)
  }

  private def _deleteStrategy(id: Int): Unit = {
    strategyDao.delete(id)
  }

  private def _listStrategies(): Unit = {
    sender ! strategyDao.list()
  }

}
