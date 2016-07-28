/**
  *
  */
package quanter.actors.persistence

import akka.actor.{Actor, Props}
import quanter.persistence.EStrategy
import quanter.persistence._
import quanter.rest.{Order, Portfolio, Strategy}
import quanter.strategies.StrategyCache

import scala.collection.mutable.ArrayBuffer
import scala.slick.driver.H2Driver.simple._


/**
  *
  */

case class SaveStrategy(obj: Strategy)
case class UpdateStrategy(id: Int, obj: Strategy)
case class DeleteStrategy(id: Int)
case class GetStrategy(id: Int)
case class ListStrategies()
case class SaveOrder(order: Order)
case class CancelOrder(id: Int)

//case class SavePortfolio(obj: EPortfolio)
//case class UpdatePortfolio(id: Int, obj: EPortfolio)
//case class GetPortfolio(id: Int)

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

  val strategyCache = new StrategyCache()

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
    case action: ListStrategies => _listStrategies()

    // 保存订单
    // 撤销订单
    case action: SaveOrder =>
    case action: CancelOrder =>
    case _ =>
  }

  val strategyDao = new StrategyDao
  val portfolioDao = new PortfolioDao()
  private def _saveStrategy(strategy: Strategy): Unit = {
    val s = EStrategy(None, strategy.name, strategy.runMode, strategy.runMode, strategy.lang.getOrElse("C#"))
    strategyDao.insert(s)
  }

  private def _getStrategy(id: Int): Unit = {
    val strategy = strategyDao.getById(id)
    sender ! strategy
  }

  private def _updateStrategy(id: Int, strategy: Strategy): Unit = {
    val s = EStrategy(Some(id), strategy.name, strategy.runMode, strategy.status, strategy.lang.getOrElse("C#"))
    val query = strategyDao.update(id, s)
  }

  private def _deleteStrategy(id: Int): Unit = {
    strategyDao.delete(id)
  }

  private def _listStrategies(): Unit = {
    val strategyArr = new ArrayBuffer[Strategy]()
    for(s <- strategyDao.list) {
      val strategy = Strategy(s.id.getOrElse(0), s.name, s.runMode, s.status, Some(s.lang), None)
      strategyArr += strategy
    }
    sender ! strategyArr
  }

}
