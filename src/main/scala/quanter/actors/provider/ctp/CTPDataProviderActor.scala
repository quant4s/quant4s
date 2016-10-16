/**
  *
  */
package quanter.actors.provider.ctp

import akka.actor.{ActorSelection, FSM, Props}
import jctp.struct.{CThostFtdcForQuoteRspField, CThostFtdcRspInfoField, CThostFtdcRspUserLoginField, _}
import quanter.actors.provider.DataProviderActor
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
  val url = "tcp://218.202.237.33:10012"

//  def start(): Unit = {
//    mds.RegisterSpi(this)
//    mds.RegisterFront(url)
//    mds.Init()
//  }

  override def addSymbol(contractCode:String): Unit = {
    super.addSymbol(contractCode)
    mds.SubscribeMarketData(Array(contractCode), 1)
  }

  override def connect(): Unit = {
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
    log.info(this + "[OnFrontConnected]")
  }

  override def OnFrontDisconnected(nReason: Int): Unit = {
    connected = false
    logined = false

    log.info(this + "[OnFrontDisconnected ] nReason=" + nReason)
  }

  override def OnRspUserLogout(pRspUserLogout: CThostFtdcUserLogoutField, pRspInfo: CThostFtdcRspInfoField, nRequestID: Int, bIsLast: Boolean): Unit =
    log.info("OnRspUserLogout : " + pRspUserLogout + ", pRspInfo=" + pRspInfo + ", nRequestID=" + nRequestID + ", bIsLast=" + bIsLast)

  override def OnRspUserLogin(pRspUserLogin: CThostFtdcRspUserLoginField, pRspInfo: CThostFtdcRspInfoField, nRequestID: Int, bIsLast: Boolean): Unit = {
    var result: LoginResult = null
    if (_isError(pRspInfo))
      result = new LoginResult(pRspInfo.ErrorID, pRspInfo.ErrorMsg)
    else {
      result = new LoginResult(0, "登录成功")
      logined = true

      // TODO: 通知登录事件
      fireEvent(TradeAccountEvent.Logined_Success)
    }
  }

  override def OnRtnDepthMarketData(pDepthMarketData: CThostFtdcDepthMarketDataField): Unit = {
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

