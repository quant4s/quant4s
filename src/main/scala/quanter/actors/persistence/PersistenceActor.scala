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
import quanter.rest.{CancelOrder, Order, Strategy, Trader}
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
//  val dbUrl = "jdbc:mysql://172.16.240.1:3306/quant4s?user=root&password=root&useUnicode=true&characterEncoding=UTF8"
//  val jdbcDriver = "com.mysql.jdbc.Driver"
//  var db = Database.forURL(dbUrl, driver=jdbcDriver)
  var db = Database.forConfig("mysql")
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
    case action: NewOrder => _saveOrder(action.order)
    case action: RemoveOrder => _cancelOrder(action.order)

    // 交易接口
    case action: NewTrader => _saveTrader(action.trader)
    case action: ListTraders => _listTraders()
    case action: DeleteTrader => _deleteTrader(action.id)
    case action: UpdateTrader => _updateTrader(action.trader)
    case _ =>
  }

  val strategyDao = new StrategyDao
  val portfolioDao = new PortfolioDao
  val traderDao = new TraderDao
  val orderDao = new OrderDao

  private def _saveStrategy(strategy: Strategy): Unit = {
    val s = EStrategy(strategy.id, strategy.name, strategy.runMode, strategy.status, strategy.lang.getOrElse("C#"))
    val s1 = strategyDao.insert(s)

    if (strategy.portfolio != None) {
      val t = strategy.portfolio.get
      portfolioDao.insert(EPortfolio(None, t.cash, new Timestamp(t.date.getTime), s.id))
    }
  }

  private def _getStrategy(id: Int): Unit = {
    val strategy = strategyDao.getById(id)
    sender ! strategy
  }

  private def _updateStrategy(id: Int, strategy: Strategy): Unit = {
    val s = EStrategy(id, strategy.name, strategy.runMode, strategy.status, strategy.lang.getOrElse("C#"))
    val query = strategyDao.update(id, s)
  }

  private def _deleteStrategy(id: Int): Unit = {
    strategyDao.delete(id)
  }

  private def _listStrategies(): Unit = {
    val strategyArr = new ArrayBuffer[Strategy]()
    for(s <- strategyDao.list) {
      val strategy = Strategy(s.id, s.name, s.runMode, s.status, Some(s.lang), None)
      strategyArr += strategy
    }
    sender ! strategyArr
  }


  // Trader
  private def _saveTrader(trader: Trader): Unit = {
    val t = ETrader(None, trader.name, trader.brokerType, trader.brokerName, trader.brokerCode, trader.brokerAccount, trader.brokerPassword, trader.brokerUri, trader.brokerServicePwd, trader.status)
    val s1 = traderDao.insert(t)
  }

  private def  _listTraders(): Unit = {
    val traderArr = new ArrayBuffer[Trader]()
    for(s <- traderDao.list) {
      val trader = Trader(s.id, s.name, s.brokerType, s.brokerName, s.brokerCode, s.brokerAccount, s.brokerPassword, s.brokerUri, s.brokerServicePwd, s.status)
      traderArr += trader
    }
    sender ! traderArr.toArray
  }

  private def _updateTrader(trader: Trader): Unit = {
    val s = ETrader(trader.id, trader.name,trader.brokerType, trader.brokerName, trader.brokerCode, trader.brokerAccount, trader.brokerPassword, trader.brokerUri, trader.brokerServicePwd, trader.status)
    val query = traderDao.update(s.id.get, s)
  }

  private def _deleteTrader(id: Int): Unit = {
    traderDao.delete(id)
  }

  // Order
  private def _saveOrder(order: Order): Unit = {
    val o = EOrder(None, order.orderNo, order.strategyId, order.tradeAccountId, order.symbol, order.orderType, order.side, "time", order.quantity, "", order.price.getOrElse(0), "RMB", order.securityExchange, 0)
    val o1 = orderDao.insert(o)
  }

  private def _cancelOrder(order: CancelOrder): Unit = {
    // FIXME: 保存委托单
    val o = EOrder(None, order.orderNo, order.strategyId, order.tradeAccountId, "", 3, 3, "time", 100, "", 0, "RMB", "", 0)
    orderDao.insert(o)
    orderDao.delete(order.orderNo)
  }
}
