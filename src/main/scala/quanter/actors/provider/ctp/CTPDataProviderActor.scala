/**
  *
  */
package quanter.actors.provider.ctp

import akka.actor.{ActorSelection, FSM, Props}
import jctp.struct.{CThostFtdcForQuoteRspField, CThostFtdcRspInfoField, CThostFtdcRspUserLoginField, _}
import quanter.actors.provider._
import quanter.actors.provider.ctp.CTPDataProviderActor._
import quanter.actors.trade.{LoginResult, TradeAccountEvent, TradeAccountMessage}
import quanter.actors.trade.TradeAccountEvent.{Disconnected => _, _}
import quanter.data.market.TradeBar

import scala.collection.mutable

/**
  *
  */
class CTPDataProviderActor extends DataProviderActor with CThostFtdcMdSpi {
  val mds = CThostFtdcMdApi.CreateFtdcMdApi("./logs/ctp/mds/", false, false)
  val url = "tcp://180.168.146.187:10031"  //"tcp://218.202.237.33:10012"
  var requestId = 1
  addSymbol("AG1612")

  override def addSymbol(contractCode:String): Unit = {
    log.debug("增加关注的证券代码")
    super.addSymbol(contractCode)
    mds.SubscribeMarketData(Array(contractCode), 1)
  }

  override def login(): Unit = {
    requestId += 1
    val reqUserLogin = new CThostFtdcReqUserLoginField()
    reqUserLogin.BrokerID = "9999"
    reqUserLogin.UserID = "071003"
    reqUserLogin.Password = "123456"
    log.info(this + "[CTP MDS Login] start login")
    mds.ReqUserLogin(reqUserLogin, requestId)
  }

  override def connect(): Unit = {
    log.info("开始连接CTP")
    mds.RegisterSpi(this)
    mds.RegisterFront(url)
    mds.Init()
  }

  def unsubQuota(contractCode: String): Unit = {
    mds.UnSubscribeMarketData(Array(contractCode), 1)
  }

  private def _isError( pRspInfo: CThostFtdcRspInfoField) = (pRspInfo != null) && (pRspInfo.ErrorID != 0)

  def fireEvent(event: TradeAccountEvent): Unit = {
  }




  // ==============================  以下 ctp 接口实现

  override def OnFrontConnected(): Unit = {
    connected = true
    self ! new ConnectedSuccess()

    log.info("CTP 连接成功")
  }

  override def OnFrontDisconnected(nReason: Int): Unit = {
    connected = false
    logined = false

    log.info("断开CTP连接 nReason=" + nReason)
  }

  override def OnRspUserLogout(pRspUserLogout: CThostFtdcUserLogoutField, pRspInfo: CThostFtdcRspInfoField, nRequestID: Int, bIsLast: Boolean): Unit =
    log.info("OnRspUserLogout : " + pRspUserLogout + ", pRspInfo=" + pRspInfo + ", nRequestID=" + nRequestID + ", bIsLast=" + bIsLast)

  override def OnRspUserLogin(pRspUserLogin: CThostFtdcRspUserLoginField, pRspInfo: CThostFtdcRspInfoField, nRequestID: Int, bIsLast: Boolean): Unit = {
    var result: LoginResult = null
    if (_isError(pRspInfo))
      result = new LoginResult(pRspInfo.ErrorID, pRspInfo.ErrorMsg)
    else {
      result = new LoginResult(0, "登录成功")
      log.info("登录行情服务器成功")
      logined = true

      self ! new LoginSuccess()
      // TODO: 通知登录事件
      fireEvent(TradeAccountEvent.Logined_Success)
    }
  }

  override def OnRtnDepthMarketData(pDepthMarketData: CThostFtdcDepthMarketDataField): Unit = {
    log.debug("数据到达")
    val bar = new TradeBar()
    bar.symbol = pDepthMarketData.InstrumentID
    bar.open = pDepthMarketData.OpenPrice
    bar.high = pDepthMarketData.HighestPrice
    bar.low = pDepthMarketData.LowestPrice
    bar.value = pDepthMarketData.LastPrice

    // 把数据送到symbol actor
    symbolSelections(bar.symbol) ! bar
  }


  //
  override def OnRspUnSubForQuoteRsp(cThostFtdcSpecificInstrumentField: CThostFtdcSpecificInstrumentField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = ???

  override def OnRspSubForQuoteRsp(cThostFtdcSpecificInstrumentField: CThostFtdcSpecificInstrumentField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = ???

  override def OnHeartBeatWarning(i: Int): Unit = ???

  override def OnRtnForQuoteRsp(cThostFtdcForQuoteRspField: CThostFtdcForQuoteRspField): Unit = ???

  override def OnRspUnSubMarketData(cThostFtdcSpecificInstrumentField: CThostFtdcSpecificInstrumentField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = ???

  override def OnRspSubMarketData(cThostFtdcSpecificInstrumentField: CThostFtdcSpecificInstrumentField, cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = ???

  override def OnRspError(cThostFtdcRspInfoField: CThostFtdcRspInfoField, i: Int, b: Boolean): Unit = ???
}

object CTPDataProviderActor {
  def props: Props = {
    Props(classOf[CTPDataProviderActor])
  }
}

