/**
  *
  */
package quanter.actors.provider.ctp

import java.util.Date

import akka.actor.Props
import jctp.struct.{CThostFtdcForQuoteRspField, CThostFtdcRspInfoField, CThostFtdcRspUserLoginField, _}
import org.quant4s.actors.provider._
import org.quant4s.actors.trade.LoginResult
import org.quant4s.actors.trade.TradeAccountEvent.{Disconnected => _}
import org.quant4s.data.market.TradeBar
import org.quant4s.mds.DataProviderActor

/**
  *
  */
class CTPDataProviderActor extends DataProviderActor with CThostFtdcMdSpi {
  val mds = CThostFtdcMdApi.CreateFtdcMdApi("./logs/ctp/mds/", false, false)
  val url = "tcp://180.168.146.187:10011"  //"tcp://218.202.237.33:10012"
  var requestId = 1

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

  def subQuote(): Unit = {
    for(s <- symbolSelections.keys) {
      mds.SubscribeMarketData(Array(s), 1)
    }
    // mds.SubscribeMarketData()
  }

  private def _isError( pRspInfo: CThostFtdcRspInfoField) = (pRspInfo != null) && (pRspInfo.ErrorID != 0)





  // ==============================  以下 ctp 接口实现

  override def OnFrontConnected(): Unit = {
    connected = true
    self ! new ConnectedSuccess()

    log.info("CTP 连接成功")
  }

  override def OnFrontDisconnected(nReason: Int): Unit = {
    connected = false
    logined = false
    self ! new DisConnectedSuccess()
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

//      self ! new AskListenedSymbol("rb1701")
//      self ! new AskListenedSymbol("ni1701")
    }
  }

  override def OnRtnDepthMarketData(pDepthMarketData: CThostFtdcDepthMarketDataField): Unit = {
    log.debug("%s MD 数据到达,价格%f".format(pDepthMarketData.InstrumentID, pDepthMarketData.LastPrice))
    val bar = new TradeBar()
    bar.symbol = pDepthMarketData.InstrumentID
    bar.open = pDepthMarketData.OpenPrice
    bar.high = pDepthMarketData.HighestPrice
    bar.low = pDepthMarketData.LowestPrice
    bar.update(pDepthMarketData.LastPrice, pDepthMarketData.BidPrice1, pDepthMarketData.AskPrice1, pDepthMarketData.Volume, pDepthMarketData.BidVolume1, pDepthMarketData.AskVolume1)
    bar.endTime = new Date(pDepthMarketData.UpdateMillisec)

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

