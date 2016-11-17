/**
  *
  */
package quanter.actors.trade.ctp

import java.util.Date

import akka.actor.Props
import jctp.struct.{CThostFtdcSettlementInfoField, _}
import quanter.actors.LoginSuccess
import quanter.actors.provider.ConnectedSuccess
import quanter.actors.strategy.StrategiesManagerActor
import quanter.actors.trade._

/**
  *
  */
class CTPBrokerageActor extends BrokerageActor with CThostFtdcTraderSpi with Trader {

  val trader = CThostFtdcTraderApi.CreateFtdcTraderApi("./logs/ctp/trader/")
  var frontId = ""
  var sessionId = ""

  override protected def login(): Unit = {
    val reqUser = new CThostFtdcReqUserLoginField()

    reqUser.BrokerID = accountInfo.brokerCode
    reqUser.UserID = accountInfo.brokerAccount
    reqUser.Password = accountInfo.brokerPassword.getOrElse("")
    reqUser.UserProductInfo = "quant4s"
    log.info("[CTP Trader Login] start login")

    trader.ReqUserLogin(reqUser, nextRequestId)
  }

  override protected def logout(): Unit = {
    val reqId = nextRequestId
    val logout = new CThostFtdcUserLogoutField()
    logout.BrokerID = accountInfo.brokerCode
    logout.UserID = accountInfo.brokerAccount
    trader.ReqUserLogout(logout, reqId)
  }

  override def connect(): Unit = {
    trader.RegisterSpi(this)
    trader.SubscribePrivateTopic(2)
    trader.SubscribePublicTopic(2)
    trader.RegisterFront(accountInfo.brokerUri)
    trader.Init()
  }

  override def OnFrontConnected(): Unit = {
    _isConnected = true
    log.info("CTP Trader 连接成功！")

    self ! new quanter.actors.ConnectedSuccess()
//    fireEvent(TradeAccountEvent.Connected_Success)
  }

  override def OnFrontDisconnected(i: Int): Unit = {
    _isConnected = false
    log.info("CTP Trader 成功断开连接！")

    fireEvent(TradeAccountEvent.Disconnected)
  }

  override def OnRspUserLogin(pRspUserLogin: CThostFtdcRspUserLoginField, pRspInfo: CThostFtdcRspInfoField, nRequestID: Int, bIsLast: Boolean): Unit = {
    log.info("CTP Trader 登录成功！")
    _isLogin = true
    var result: LoginResult = null
    if (_isError(pRspInfo))
    {
      result = new LoginResult(pRspInfo.ErrorID, pRspInfo.ErrorMsg)
    }
    else
    {
      self ! new LoginSuccess()
      if (pRspUserLogin != null)
      {
        this.frontId = String.valueOf(pRspUserLogin.FrontID)
        this.sessionId = String.valueOf(pRspUserLogin.SessionID)
      }
      log.info(s"CTP %s 登录成功".format(accountInfo.name))
      result = new LoginResult(0, "登录成功")
      if (pRspUserLogin.FFEXTime.startsWith("-")) {
        pRspUserLogin.FFEXTime = pRspUserLogin.LoginTime
      }

      val cqs = new CThostFtdcQrySettlementInfoConfirmField()
      cqs.BrokerID = accountInfo.brokerCode
      cqs.InvestorID = accountInfo.brokerAccount
      trader.ReqQrySettlementInfoConfirm(cqs, nRequestID + 1)
      val cs = new CThostFtdcSettlementInfoConfirmField()
      cs.BrokerID = accountInfo.brokerCode
      cs.InvestorID = accountInfo.brokerAccount
      trader.ReqSettlementInfoConfirm(cs, nRequestID + 2)
    }
  }

  override def OnRspUserLogout(pRspUserLogout: CThostFtdcUserLogoutField, pRspInfo: CThostFtdcRspInfoField, nRequestID: Int, bIsLast: Boolean): Unit = {
    log.info("OnRspUserLogout : " + pRspUserLogout + ", pRspInfo=" + pRspInfo + ", nRequestID=" + nRequestID + ", bIsLast=" + bIsLast)
    _isLogin = false
  }


  /**
    * 报单录入错误回报。由交易托管系统主动通知客户端，该方法会被调用。
 *
    * @param pInputOrder
    * @param pRspInfo
    */
  override def OnErrRtnOrderInsert(pInputOrder: CThostFtdcInputOrderField, pRspInfo: CThostFtdcRspInfoField): Unit = {
    if (_isError(pRspInfo))
    {
      if (pInputOrder != null) {
        // processOrderError0(pInputOrder.OrderRef, pRspInfo.ErrorID + "", pRspInfo.ErrorMsg)
      } else {
        log.error("28: errorID=" + pRspInfo.ErrorID + ", errorMsg=" + pRspInfo.ErrorMsg)
      }
    } else
    log.info(pInputOrder + "")
  }

  /**
    * 报单录入应答。当客户端发出过报单录入指令后，交易托管系统返回响应时，该方法会被调用。
 *
    * @param pInputOrder
    * @param pRspInfo
    * @param nRequestID
    * @param bIsLast
    */
  override def OnRspOrderInsert(pInputOrder: CThostFtdcInputOrderField, pRspInfo: CThostFtdcRspInfoField, nRequestID: Int, bIsLast: Boolean): Unit = {
    if (_isError(pRspInfo)) {
      if (pInputOrder != null) {
        // 通知报单失败
        // processOrderError0(pInputOrder.OrderRef, pRspInfo.ErrorID + "", pRspInfo.ErrorMsg);
      } else {
        log.error("1023: errorID=" + pRspInfo.ErrorID + ", errorMsg=" + pRspInfo.ErrorMsg)
      }
    } else {
      // val result = new OrderStatusResult("", pInputOrder.OrderRef, pInputOrder.Direction, THOST_FTDC_OST_Unknown)
    }
  }

  /**
    * 报单回报。当客户端进行报单录入、报单操作及其它原因（如部分成交）导致报单状态发生变化时，
    * 交易托管系统会主动通知客户端，该方法会被调用
 *
    * @param pOrder
    */
  override def OnRtnOrder(pOrder: CThostFtdcOrderField): Unit = {
    // 根据本地委托ID，找到策略ID
    val localId = pOrder.OrderRef
    val strategyId =  getStrategyId(localId)
    val strategyRef = context.actorSelection("/user/%s/%s".format(StrategiesManagerActor.path, strategyId))

    val id = nextResultId
    val orderSysID = pOrder.OrderSysID
    val direction = pOrder.Direction.toInt
    val mdDate = new Date()
    val status = pOrder.OrderStatus.toString
    val accountId = ""

    val result = new OrderStatusResult(id, localId, mdDate, Some(accountId), direction, orderSysID, status)

    strategyRef ! result
  }

  /**
    * 成交回报。当发生成交时交易托管系统会通知客户端，该方法会被调用。
 *
    * @param pTrade
    */
  override def OnRtnTrade(pTrade: CThostFtdcTradeField): Unit = {
    // 根据本地委托ID，找到策略ID
    val localId = pTrade.OrderLocalID
    val strategyId =  getStrategyId(localId)
    val strategyRef = context.actorSelection("/user/%s/%s".format(StrategiesManagerActor.path, strategyId))

    // 修改策略的内存持仓数据, 由策略负责更新到成交回报数据库
    val result = new OrderDealResult(nextResultId, localId, new Date(), Some(accountInfo.brokerAccount), pTrade.InstrumentID, pTrade.Direction.toInt,
      pTrade.OffsetFlag, pTrade.TradeID, pTrade.Price, pTrade.Volume, pTrade.OrderSysID)

    strategyRef ! result
  }

  override def OnErrRtnOrderAction(cThostFtdcOrderActionField: CThostFtdcOrderActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRspOrderAction(cThostFtdcInputOrderActionField: CThostFtdcInputOrderActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspError(pRspInfo: CThostFtdcRspInfoField, nRequestID: Int, b: Boolean): Unit = log.info("OnRspError : " + pRspInfo + ", nRequestID=" + nRequestID)

  override def OnRspQryInstrumentMarginRate(cThostFtdcInstrumentMarginRateField: CThostFtdcInstrumentMarginRateField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryInstrumentCommissionRate(cThostFtdcInstrumentCommissionRateField: CThostFtdcInstrumentCommissionRateField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryInstrument(cThostFtdcInstrumentField: CThostFtdcInstrumentField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryInvestor(cThostFtdcInvestorField: CThostFtdcInvestorField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryInvestorPosition(cThostFtdcInvestorPositionField: CThostFtdcInvestorPositionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryInvestorPositionCombineDetail(cThostFtdcInvestorPositionCombineDetailField: CThostFtdcInvestorPositionCombineDetailField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQrySettlementInfoConfirm(cThostFtdcSettlementInfoConfirmField: CThostFtdcSettlementInfoConfirmField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryTradingAccount(cThostFtdcTradingAccountField: CThostFtdcTradingAccountField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspSettlementInfoConfirm(cThostFtdcSettlementInfoConfirmField: CThostFtdcSettlementInfoConfirmField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}


  /**
    * 请求查询行情响应。当客户端发出请求查询行情指令后，交易托管系统返回响应时，该方法会被调用。
 *
    * @param cThostFtdcDepthMarketDataField
    * @param cThostFtdcRspInfoField
    * @param i
    * @param b
    */
  override def OnRspQryDepthMarketData(cThostFtdcDepthMarketDataField: CThostFtdcDepthMarketDataField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {
    // TODO: 更新持仓盈亏
  }

  private def _isError(pRspInfo: CThostFtdcRspInfoField) = (pRspInfo != null) && (pRspInfo.ErrorID != 0)

  // =================================未实现的接口===============================
  override def OnRspQryNotice(cThostFtdcNoticeField: CThostFtdcNoticeField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspRemoveParkedOrderAction(cThostFtdcRemoveParkedOrderActionField: CThostFtdcRemoveParkedOrderActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnCancelAccountByBank(cThostFtdcCancelAccountField: CThostFtdcCancelAccountField): Unit = {}

  override def OnRtnChangeAccountByBank(cThostFtdcChangeAccountField: CThostFtdcChangeAccountField): Unit = {}

  override def OnRspQryCFMMCTradingAccountKey(cThostFtdcCFMMCTradingAccountKeyField: CThostFtdcCFMMCTradingAccountKeyField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspRemoveParkedOrder(cThostFtdcRemoveParkedOrderField: CThostFtdcRemoveParkedOrderField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryProductExchRate(cThostFtdcProductExchRateField: CThostFtdcProductExchRateField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspForQuoteInsert(cThostFtdcInputForQuoteField: CThostFtdcInputForQuoteField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryProduct(cThostFtdcProductField: CThostFtdcProductField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryOptionInstrCommRate(cThostFtdcOptionInstrCommRateField: CThostFtdcOptionInstrCommRateField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryExchangeRate(cThostFtdcExchangeRateField: CThostFtdcExchangeRateField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryForQuote(cThostFtdcForQuoteField: CThostFtdcForQuoteField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnOpenAccountByBank(cThostFtdcOpenAccountField: CThostFtdcOpenAccountField): Unit = {}

  override def OnErrRtnBatchOrderAction(cThostFtdcBatchOrderActionField: CThostFtdcBatchOrderActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRtnFromBankToFutureByBank(cThostFtdcRspTransferField: CThostFtdcRspTransferField): Unit = {}

  override def OnRspQueryBankAccountMoneyByFuture(cThostFtdcReqQueryAccountField: CThostFtdcReqQueryAccountField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnErrRtnRepealBankToFutureByFutureManual(cThostFtdcReqRepealField: CThostFtdcReqRepealField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRspQryProductGroup(cThostFtdcProductGroupField: CThostFtdcProductGroupField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnRepealFromFutureToBankByFuture(cThostFtdcRspRepealField: CThostFtdcRspRepealField): Unit = {}

  override def OnErrRtnQueryBankBalanceByFuture(cThostFtdcReqQueryAccountField: CThostFtdcReqQueryAccountField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRspQryParkedOrderAction(cThostFtdcParkedOrderActionField: CThostFtdcParkedOrderActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnRepealFromBankToFutureByFuture(cThostFtdcRspRepealField: CThostFtdcRspRepealField): Unit = {}

  override def OnRspQryExchange(cThostFtdcExchangeField: CThostFtdcExchangeField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryTradingNotice(cThostFtdcTradingNoticeField: CThostFtdcTradingNoticeField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQuoteAction(cThostFtdcInputQuoteActionField: CThostFtdcInputQuoteActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspFromFutureToBankByFuture(cThostFtdcReqTransferField: CThostFtdcReqTransferField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryInvestorPositionDetail(cThostFtdcInvestorPositionDetailField: CThostFtdcInvestorPositionDetailField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryTradingCode(cThostFtdcTradingCodeField: CThostFtdcTradingCodeField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnExecOrder(cThostFtdcExecOrderField: CThostFtdcExecOrderField): Unit = {}

  override def OnRspUserPasswordUpdate(cThostFtdcUserPasswordUpdateField: CThostFtdcUserPasswordUpdateField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnHeartBeatWarning(i: Int): Unit = {}

  override def OnRtnRepealFromFutureToBankByBank(cThostFtdcRspRepealField: CThostFtdcRspRepealField): Unit = {}

  override def OnRtnCombAction(cThostFtdcCombActionField: CThostFtdcCombActionField): Unit = {}

  override def OnRspFromBankToFutureByFuture(cThostFtdcReqTransferField: CThostFtdcReqTransferField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryBrokerTradingParams(cThostFtdcBrokerTradingParamsField: CThostFtdcBrokerTradingParamsField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnErrRtnForQuoteInsert(cThostFtdcInputForQuoteField: CThostFtdcInputForQuoteField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRspQryExchangeMarginRateAdjust(cThostFtdcExchangeMarginRateAdjustField: CThostFtdcExchangeMarginRateAdjustField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnQuote(cThostFtdcQuoteField: CThostFtdcQuoteField): Unit = {}

  override def OnRspQryEWarrantOffset(cThostFtdcEWarrantOffsetField: CThostFtdcEWarrantOffsetField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryOptionInstrTradeCost(cThostFtdcOptionInstrTradeCostField: CThostFtdcOptionInstrTradeCostField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQuoteInsert(cThostFtdcInputQuoteField: CThostFtdcInputQuoteField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspBatchOrderAction(cThostFtdcInputBatchOrderActionField: CThostFtdcInputBatchOrderActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryExecOrder(cThostFtdcExecOrderField: CThostFtdcExecOrderField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryAccountregister(cThostFtdcAccountregisterField: CThostFtdcAccountregisterField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnErrRtnExecOrderAction(cThostFtdcExecOrderActionField: CThostFtdcExecOrderActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRtnForQuoteRsp(cThostFtdcForQuoteRspField: CThostFtdcForQuoteRspField): Unit = {}

  override def OnRspExecOrderAction(cThostFtdcInputExecOrderActionField: CThostFtdcInputExecOrderActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryParkedOrder(cThostFtdcParkedOrderField: CThostFtdcParkedOrderField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryContractBank(cThostFtdcContractBankField: CThostFtdcContractBankField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnErrRtnCombActionInsert(cThostFtdcInputCombActionField: CThostFtdcInputCombActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRspParkedOrderInsert(cThostFtdcParkedOrderField: CThostFtdcParkedOrderField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspParkedOrderAction(cThostFtdcParkedOrderActionField: CThostFtdcParkedOrderActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQueryCFMMCTradingAccountToken(cThostFtdcQueryCFMMCTradingAccountTokenField: CThostFtdcQueryCFMMCTradingAccountTokenField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryTransferBank(cThostFtdcTransferBankField: CThostFtdcTransferBankField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnTradingNotice(cThostFtdcTradingNoticeInfoField: CThostFtdcTradingNoticeInfoField): Unit = {}

  override def OnRspQrySecAgentACIDMap(cThostFtdcSecAgentACIDMapField: CThostFtdcSecAgentACIDMapField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnErrRtnExecOrderInsert(cThostFtdcInputExecOrderField: CThostFtdcInputExecOrderField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRtnInstrumentStatus(cThostFtdcInstrumentStatusField: CThostFtdcInstrumentStatusField): Unit = {}

  override def OnRspTradingAccountPasswordUpdate(cThostFtdcTradingAccountPasswordUpdateField: CThostFtdcTradingAccountPasswordUpdateField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnRepealFromBankToFutureByBank(cThostFtdcRspRepealField: CThostFtdcRspRepealField): Unit = {}


  override def OnRtnQueryBankBalanceByFuture(cThostFtdcNotifyQueryAccountField: CThostFtdcNotifyQueryAccountField): Unit = {}

  override def OnRspQryBrokerTradingAlgos(cThostFtdcBrokerTradingAlgosField: CThostFtdcBrokerTradingAlgosField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnCFMMCTradingAccountToken(cThostFtdcCFMMCTradingAccountTokenField: CThostFtdcCFMMCTradingAccountTokenField): Unit = {}

  override def OnErrRtnRepealFutureToBankByFutureManual(cThostFtdcReqRepealField: CThostFtdcReqRepealField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRspExecOrderInsert(cThostFtdcInputExecOrderField: CThostFtdcInputExecOrderField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnRepealFromFutureToBankByFutureManual(cThostFtdcRspRepealField: CThostFtdcRspRepealField): Unit = {}

  override def OnRspQryCombInstrumentGuard(cThostFtdcCombInstrumentGuardField: CThostFtdcCombInstrumentGuardField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnErrorConditionalOrder(cThostFtdcErrorConditionalOrderField: CThostFtdcErrorConditionalOrderField): Unit = {}

  override def OnRspAuthenticate(cThostFtdcRspAuthenticateField: CThostFtdcRspAuthenticateField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspCombActionInsert(cThostFtdcInputCombActionField: CThostFtdcInputCombActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnFromBankToFutureByFuture(cThostFtdcRspTransferField: CThostFtdcRspTransferField): Unit = {}

  override def OnRspQryInvestorProductGroupMargin(cThostFtdcInvestorProductGroupMarginField: CThostFtdcInvestorProductGroupMarginField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryTrade(cThostFtdcTradeField: CThostFtdcTradeField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnFromFutureToBankByBank(cThostFtdcRspTransferField: CThostFtdcRspTransferField): Unit = {}

  override def OnErrRtnFutureToBankByFuture(cThostFtdcReqTransferField: CThostFtdcReqTransferField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRspQryTransferSerial(cThostFtdcTransferSerialField: CThostFtdcTransferSerialField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnErrRtnBankToFutureByFuture(cThostFtdcReqTransferField: CThostFtdcReqTransferField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRspQueryMaxOrderVolume(cThostFtdcQueryMaxOrderVolumeField: CThostFtdcQueryMaxOrderVolumeField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryOrder(cThostFtdcOrderField: CThostFtdcOrderField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnErrRtnQuoteInsert(cThostFtdcInputQuoteField: CThostFtdcInputQuoteField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnErrRtnQuoteAction(cThostFtdcQuoteActionField: CThostFtdcQuoteActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField): Unit = {}

  override def OnRspQryExchangeMarginRate(cThostFtdcExchangeMarginRateField: CThostFtdcExchangeMarginRateField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnFromFutureToBankByFuture(cThostFtdcRspTransferField: CThostFtdcRspTransferField): Unit = {}

  override def OnRspQryQuote(cThostFtdcQuoteField: CThostFtdcQuoteField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRtnRepealFromBankToFutureByFutureManual(cThostFtdcRspRepealField: CThostFtdcRspRepealField): Unit = {}

  override def OnRspQrySettlementInfo(cThostFtdcSettlementInfoField: CThostFtdcSettlementInfoField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}

  override def OnRspQryCombAction(cThostFtdcCombActionField: CThostFtdcCombActionField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = {}


  // ======================Trader Implement============================
  override def queryCapital(): Unit = {
    val reqId = nextRequestId
    val req = new CThostFtdcQryTradingAccountField()
    req.BrokerID = accountInfo.brokerCode
    req.InvestorID = accountInfo.brokerAccount
    trader.ReqQryTradingAccount(req, reqId)
  }

  override def queryPosition(symbol: String): Unit = {
    val reqId = nextRequestId
    val pos = new CThostFtdcQryInvestorPositionField()
    pos.BrokerID = accountInfo.brokerCode
    pos.InvestorID = accountInfo.brokerAccount
    pos.InstrumentID = symbol
    this.trader.ReqQryInvestorPosition(pos, reqId)
    log.debug("账户%s, 查询%s持仓".format(accountInfo.name, symbol))
  }

  override def cancel(order: Order): Unit = {
    val requestId = nextRequestId
    val cancel = new CThostFtdcInputOrderActionField()
    cancel.BrokerID = accountInfo.brokerCode
    cancel.InvestorID = accountInfo.brokerAccount
    cancel.InstrumentID = order.contractCode

    cancel.OrderRef = order.localeId
    cancel.FrontID = Integer.parseInt(order.frontId)
    cancel.SessionID = Integer.parseInt(order.sessionId)
    cancel.ActionFlag = '0'
    trader.ReqOrderAction(cancel, requestId)
    log.debug("账户%s, 取消%s订单".format(accountInfo.name, order.contractCode))
  }

  override def order(order: Order): Unit = {
    order.frontId = frontId
    order.sessionId = sessionId

    val req = new CThostFtdcInputOrderField()
    req.BrokerID = accountInfo.brokerCode
    req.InstrumentID = order.contractCode
    req.VolumeTotalOriginal = order.volume
    req.Direction = if(order.direction == 0) '0' else '1'
    req.CombOffsetFlag = order.COFlag.toString
    req.CombHedgeFlag = "1"
    req.ContingentCondition = '1'
    req.VolumeCondition = '1'
    req.MinVolume = 1
    req.ForceCloseReason = '0'
    req.IsAutoSuspend = false
    req.UserForceClose = false
    req.OrderRef = order.localeId

    //
    req.OrderPriceType = '1'
    req.LimitPrice = 1.0
    req.TimeCondition = '1'

    trader.ReqOrderInsert(req, nextRequestId)
  }

}

object CTPBrokerageActor {
  def props: Props = {
    Props(classOf[CTPBrokerageActor])
  }
}
