/**
  *
  */
package quanter.actors.persistence

import java.sql.Timestamp

import scala.collection.mutable.ArrayBuffer
import scala.slick.driver.MySQLDriver.simple._
import akka.actor.{Actor, Props}
import quanter.actors._
import quanter.persistence._
import quanter.rest.{Strategy, Trader}
import quanter.strategies.StrategyCache

// import scala.slick.driver.H2Driver.simple._


/**
  *
  */


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
//  val dbUrl = "jdbc:h2:mem:test"
//  val jdbcDriver = "org.h2.Driver"
  val dbUrl = "jdbc:mysql://172.16.240.1:3306/quant4s?user=root&password=root&useUnicode=true&characterEncoding=UTF8"
  val jdbcDriver = "com.mysql.jdbc.Driver"
  var db = Database.forURL(dbUrl, driver=jdbcDriver)
  implicit val session = db.createSession()

  val strategyCache = new StrategyCache()

  val ddl = gStrategies.ddl ++ gPortfolios.ddl ++ gStockHoldings.ddl ++ gTransactions.ddl ++ gOrders.ddl ++ gTraders.ddl
  // TODO: 如果是第一次启动
  if(true) {
    ddl.drop
    ddl.create
  }


  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    session.close()
    super.postStop()
  }

  override def receive: Receive = {
    case action: NewStrategy => _saveStrategy(action.strategy)
    case action: DeleteStrategy => _deleteStrategy(action.id)
    case action: UpdateStrategy => _updateStrategy(action.strategy.id, action.strategy)
    case action: GetStrategy => _getStrategy(action.id)
    case action: ListStrategies => _listStrategies()

    // 保存订单
    // 撤销订单
    case action: NewOrder =>
    case action: CancelOrder =>

    // 交易接口
    case action: NewTrader => _saveTrader(action.trader)
    case actoin: ListTraders => _listTraders()
    case _ =>
  }

  val strategyDao = new StrategyDao
  val portfolioDao = new PortfolioDao
  val traderDao = new TraderDao

  private def _saveStrategy(strategy: Strategy): Unit = {
    val s = EStrategy(None, strategy.name, strategy.runMode, strategy.status, strategy.lang.getOrElse("C#"))
    val s1 = strategyDao.insert(s)

    if (strategy.portfolio != None) {
      val t = strategy.portfolio.get
      portfolioDao.insert(EPortfolio(None, t.cash, new Timestamp(t.date.getTime), s1.id.get))
    }
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


  private def _saveTrader(trader: Trader): Unit = {
    val t = ETrader(None, trader.name, trader.brokerType, trader.brokerName, trader.brokerCode, trader.brokerAccount, trader.brokerPassword, trader.brokerUri, trader.brokerServicePwd, trader.status)
    val s1 = traderDao.insert(t)
  }

  private def _listTraders(): Unit = {
    val traderArr = new ArrayBuffer[Trader]()
    for(s <- traderDao.list) {
      val trader = Trader(s.id, s.name, s.brokerType, s.brokerName, s.brokerCode, s.brokerAccount, s.brokerPassword, s.brokerUri, s.brokerServicePwd, s.status)
      traderArr += trader
    }
    sender ! traderArr.toArray
  }
}
