package org.quant4s

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
    * @param id 自动编号
    * @param orderNo  用户生成的订单编号（策略内部唯一）
    * @param strategyId 策略编号
    * @param accountId 交易账户编号
    * @param symbol 证券代码 | 合约编号
    * @param orderType  订单类型 （限价 | 最新价 | BUY1 | BUY2 | SELL1）
    * @param side
    * @param transactTime 委托时间
    * @param quantity 委托数量
    * @param price 委托价格（非限价时，为0）
    * @param currency 当前币种(RMB)
    * @param exchange 交易所（）
    * @param status 委托单状态   0：待报，1：已报 2：废单  3：撤单 4：部分成交 5：全部成交
    */
  case class EOrder(id: Option[Int], orderNo: Int, strategyId: Int, accountId: Int, symbol: String, orderType: Int, side: Int, positionEffect: Int,
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


  /**
    * 当日交易记录
    * @param strategyId 策略编号
    * @param accountId  账户编号
    * @param productCode 产品类型编号
    * @param marketCode 市场代码
    * @param orderNo 内部委托单编号
    * @param code 证券代码
    * @param executionQuantity 成交数量
    * @param executionTime 成交时间
    * @param executionNo 成交编号
    * @param executionPrice 成交价格
    */
  case class ETradeTransaction(id: Option[Int], strategyId: Int, accountId: Int, productCode: String, marketCode: String,
                               orderId: Int, code: String, executionQuantity: Int, executionTime: Date,
                               executionNo: String, executionPrice: Double)
  class ETradeTransactions(tag: Tag) extends Table[ETradeTransaction](tag, "TRADE_TRANSACTION") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def strategyId = column[Int]("STRATEGY_ID")
    def accountId = column[Int]("ACCOUNT_ID")
    def productCode = column[String]("PRODUCT_CODE")
    def marketCode = column[String]("MARKET_CODE")
    def orderId = column[Int]("ORDER_ID")
    def code  = column[String]("CODE")
    def executionQuantity = column[Int]("EXECUTION_QUANTITY")
    def executionTime = column[Date]("EXECUTION_TIME")
    def executionNo = column[String]("EXECUTION_NO")
    def executionPrice = column[Double]("EXECUTION_PRICE")

    def * = (id.?, strategyId, accountId, productCode, marketCode, orderId, code, executionQuantity,
      executionTime, executionNo, executionPrice) <>(ETradeTransaction.tupled, ETradeTransaction.unapply)
  }
  val gTradeTransactions = TableQuery[ETradeTransactions]

  /**
    * 交易日历
    * @param id
    * @param year
    * @param month
    * @param day
    * @param name
    */
  case class ETradingCalendar(id: Option[Int], year: Int, month: Int, day: Int, name: String)
  class ETradingCalendars(tag: Tag) extends Table[ETradingCalendar](tag, "TRADING_CALENDAR") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def year = column[Int]("YEAR")
    def month = column[Int]("MONTH")
    def day = column[Int]("DAY")
    def name = column[String]("NAME")
    def * = (id.?, year, month, day, name) <> (ETradingCalendar.tupled, ETradingCalendar.unapply)
  }
  val gTradingCalendars = TableQuery[ETradingCalendars]

  /**
    * 币种表
    * @param code
    * @param name
    */
  case class ECurrency(code: String, name: String)
  class ECurrencies(tag: Tag) extends Table[ECurrency](tag, "CURRENCY") {
    def code = column[String]("CODE", O.PrimaryKey)
    def name = column[String]("NAME")

    def * = (code, name) <> (ECurrency.tupled, ECurrency.unapply)
  }
  val gCurrencies = TableQuery[ECurrencies]

  case class EExchangeRate(id: Int, srcCode: String, destCode: String, rate: Double)
  class EExchangeRates(tag: Tag) extends Table[EExchangeRate](tag, "EXCHANGE_RATE") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def srcCode = column[String]("SRC_CODE")
    def destCode = column[String]("DEST_CODE")
    def rate = column[Double]("RATE")

    def * = (id,srcCode, destCode, rate) <> (EExchangeRate.tupled, EExchangeRate.unapply)
  }
  val gExchangeRates = TableQuery[EExchangeRates]

  var tableList = Map("TRADE_TRANSACTION" -> gTradeTransactions, "TRADERS" -> gTraders, "TRANSACTION"-> gTransactions, "ORDERS" -> gOrders, "POSITION" ->gPositions, "PORTFOLIOS" -> gPortfolios, "STRATEGIES" -> gStrategies)

}
