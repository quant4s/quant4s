package quanter

import java.util.Date

import com.typesafe.config.ConfigFactory

import scala.slick.driver.{H2Driver, JdbcProfile, MySQLDriver, PostgresDriver}


/**
  *
  */
package object persistence {

  def _getDriver = {
    val TEST = "test"
    val DEV = "dev"
    val PROD = "prod"
    val mode = ConfigFactory.load().getString("quant4s.runMode")
    mode match {
      case TEST => H2Driver
      case DEV => MySQLDriver
      case PROD => PostgresDriver
      case _ => MySQLDriver
    }
  }
    val profile: JdbcProfile = _getDriver

    import profile.simple._

  implicit val JavaUtilDateMapper =
    MappedColumnType.base[java.util.Date, java.sql.Timestamp] (
      d => new java.sql.Timestamp(d.getTime),
      d => new java.util.Date(d.getTime))

    case class EStrategy(id: Int, name: String, runMode: Int, status: Int, lang: String) {}
    class EStrategies(tag: Tag) extends Table[EStrategy](tag, "STRATEGIES") {
      def id = column[Int]("ID", O.PrimaryKey)
      def name = column[String]("NAME")
      def runMode = column[Int]("RUNMODE")
      def status = column[Int]("STATUS")
      def lang = column[String]("LANG")

      def * = (id, name, runMode, status, lang) <>(EStrategy.tupled, EStrategy.unapply)
    }
    val gStrategies = TableQuery[EStrategies]

    case class EPortfolio(id: Option[Int], cash: Double, date: Date, strategyId: Int) {}
    class EPortfolios(tag: Tag) extends Table[EPortfolio](tag, "PORTFOLIOS") {
      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
      def cash = column[Double]("CASH")
      def date = column[Date]("HOLDINGDATE")
      def strategyId = column[Int]("STRATEGY_ID")
      def strategy = foreignKey("STRATEGY_FK", strategyId, gStrategies)(_.id)
      def * = (id.?, cash, date, strategyId) <>(EPortfolio.tupled, EPortfolio.unapply)
    }
    val gPortfolios = TableQuery[EPortfolios]


  /**
    *
    * @param id
    * @param strategyId
    * @param symbol
    */
    case class EPosition(id: Option[Int], strategyId: Int, accountId: Int, symbol: String, side: Int, positionEffect: Int,
                         quantity: Int, frozenQuantity: Int, price: Double, cost: Double, profit: Double, transTime: Date) {}
    class EPositions(tag: Tag) extends Table[EPosition](tag, "POSITION") {
      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
      def symbol = column[String]("SYMBOL")
      def strategyId = column[Int]("STRATEGY_ID")
      def accountId = column[Int]("ACCOUNT_ID")
      def side = column[Int]("SIDE")
      def positionEffect = column[Int]("POS_EFF")
      def quantity = column[Int]("QUANTITY")
      def frozenQuantity = column[Int]("FROZEN_QUANTITY")
      def price = column[Double]("PRICE")
      def cost = column[Double]("COST")
      def profit = column[Double]("PROFIT")
      def transTime = column[Date]("TRANSTIME")
      def * = (id.?, strategyId, accountId, symbol, side, positionEffect, quantity, frozenQuantity, price, cost, profit, transTime) <>(EPosition.tupled, EPosition.unapply)
    }
    val gPositions = TableQuery[EPositions]

  /**
    * 委托单
 *
    * @param id
    * @param orderNo
    * @param strategyId
    * @param tradeAccountId
    * @param symbol
    * @param orderType
    * @param side
    * @param transactTime
    * @param quantity
    * @param price
    * @param currency
    * @param exchange
    * @param status
    */
    case class EOrder(id: Option[Int], orderNo: Int, strategyId: Int, tradeAccountId: Int, symbol: String, orderType: Int, side: Int, positionEffect: Int,
                      transactTime: Date, quantity: Int, price: Double, currency: String, exchange: String, status: Int)
    class EOrders(tag: Tag) extends Table[EOrder](tag, "ORDERS") {
      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
      def orderNo = column[Int]("ORDER_NO")
      def symbol = column[String]("SYMBOL")
      def accountId = column[Int]("ACCOUNT_ID")
      def strategyId = column[Int]("STRATEGY_ID")
      def orderType = column[Int]("ORDER_TYPE")
      def side = column[Int]("SIDE")
      def positionEffect = column[Int]("POS_EFFE")
      def transactTime = column[Date]("TRANSACT_TIME")
      def quantity = column[Int]("QUANTITY")
      def price = column[Double]("PRICE")
      def currency = column[String]("CURRENCY")
      def exchange = column[String]("SECURITY_EXCHANGE")
      def status = column[Int]("STATUS")

      def * = (id.?, orderNo, strategyId, accountId, symbol, orderType, side, positionEffect, transactTime, quantity, price, currency, exchange, status) <>(EOrder.tupled, EOrder.unapply)
    }
    val gOrders = TableQuery[EOrders]


    case class ETransaction(id: Option[Int], orderNo: Int, strategyId: Int, accountId: Int, symbol: String, side: Int, positionEffect: Int,
                            transactTime: Date, quantity: Int, price: Double)
    class ETransactions(tag: Tag) extends Table[ETransaction](tag, "TRANSACTION") {
      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
      def orderNo = column[Int]("ORDER_NO")
      def symbol = column[String]("SYMBOL")
      def accountId = column[Int]("ACCOUNT_ID")
      def strategyId = column[Int]("STRATEGY_ID")
      def side = column[Int]("SIDE")
      def positionEffect = column[Int]("POS_EFFE")
      def transactTime = column[Date]("TRANSACT_TIME")
      def quantity = column[Int]("QUANTITY")
      def price = column[Double]("PRICE")

      def * = (id.?, orderNo, strategyId, accountId, symbol, side, positionEffect, transactTime, quantity, price) <>(ETransaction.tupled, ETransaction.unapply)
    }
  val gTransactions = TableQuery[ETransactions]

    case class ETrader(id: Option[Int], name: String, brokerType: String, brokerName: String, brokerCode: String, brokerAccount: String, brokerPassword: Option[String], brokerUri: String, brokerServicePwd: Option[String], status: Int = 0)
    class ETraders(tag: Tag) extends Table[ETrader](tag, "TRADERS") {
      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
      def name = column[String]("NAME")
      def brokerType = column[String]("BROKERTYPE")
      def brokerName = column[String]("BROKERNAME")
      def brokerCode = column[String]("BROKERCODE")
      def brokerAccount = column[String]("BROKERACCOUNT")
      def brokerPassword = column[String]("BROKERPASSWORD", O.Nullable, O.Default[String](""))
      def brokerUri = column[String]("BROKERURI")
      def brokerServicePwd = column[String]("BROKERSERVICEPWD", O.Nullable, O.Default[String](""))
      def status = column[Int]("STATUS")
      def * = (id.?, name, brokerType, brokerName, brokerCode, brokerAccount, brokerPassword ?, brokerUri, brokerServicePwd ?, status) <>(ETrader.tupled, ETrader.unapply)
    }
    val gTraders = TableQuery[ETraders]






  }
