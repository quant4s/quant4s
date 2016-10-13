/**
  *
  */
package quanter.brokerages.hensun.t2

import com.hundsun.mcapi.MCServers
import com.hundsun.mcapi.interfaces.ISubscriber
import com.hundsun.mcapi.subscribe.MCSubscribeParameter
import com.hundsun.t2sdk.common.core.context.ContextUtil
import com.hundsun.t2sdk.common.share.dataset.DatasetService
import com.hundsun.t2sdk.impl.client.T2Services
import com.hundsun.t2sdk.interfaces.{IClient, T2SDKException}
import com.hundsun.t2sdk.interfaces.share.dataset.{IDataset, IDatasets}
import com.hundsun.t2sdk.interfaces.share.event.{EventReturnCode, EventType, IEvent}
import quanter.brokerages.Brokerage
import quanter.rest.Trader

class T2Brokerage(pname: String) extends Brokerage(pname){
  def this() {
    this("")
  }
  val server = T2Services.getInstance()
  var subscriber: ISubscriber = null
  var subscribeid: Int = 0
  var client: IClient = null
  val TIMEOUT = 10000

  override def isConnected: Boolean = ???

  override def buy(code: String, price: Double, quantity: Int): Unit = ???

  override def sell(code: String, price: Double, quantity: Int): Unit = ???
  override var accountInfo: Trader = new Trader(Some(1), "T2 TEST", "BROKERTYPE", "TPY", "0030", "00250010", Some("tpyzq88888888"), "180.169.57.86:9999", None, 0)

  override def connect: Unit = {
    logger.info("链接T2后台服务 订阅主推消息")
    server.init()
    server.start()
    client = server.getClient("as_ufx")

    MCServers.MCInit()
    subscriber = MCServers.GetSubscriber()
    val subParam = new MCSubscribeParameter()
    subParam.SetTopicName("ufx_topic") // ufx成交回报固定主题
    subParam.SetFromNow(true)
    subParam.SetReplace(false)
    subParam.SetFilter("operator_no", accountInfo.brokerAccount)
    val dataset = DatasetService.getDefaultInstance().getDataset()
    dataset.addColumn("login_operator_no")
    dataset.addColumn("password")
    dataset.appendRow()
    dataset.updateString("login_operator_no", accountInfo.brokerAccount)
    dataset.updateString("password", accountInfo.brokerPassword.getOrElse("88888888"))
    subParam.SetBizCheck(dataset)
    subscribeid = subscriber.SubscribeTopic(subParam, 3000)
    if (subscribeid < 0)
      logger.error("订阅主题失败 ret[" + subscribeid + "]")

    _login
  }

  override def disconnect: Unit = {
    logger.info("后台链接断开")
    val ret = subscriber.CancelSubscribeTopic(subscribeid)
    if (ret < 0) {
      logger.error("取消订阅主题失败")
      // throw new Exception("取消订阅主题失败 ret[" + ret + "]")
    }
    MCServers.Destroy()
    server.stop()
  }

  override def keep: Unit = ???

  private def _login: Unit = {
    logger.info("开始登陆")
    val result: IDatasets = _callSerivce(10001, _getLoginPack())
    val head: IDataset = result.getDataset(0)
    val errCode = head.getInt("ErrorCode")
  }

  def  _getLoginPack(): IDataset = {
    val OPERATOR_NO = "10000"
    val PASSWD = "0"
    val dataset: IDataset = DatasetService.getDefaultInstance().getDataset()
    dataset.addColumn("operator_no")
    dataset.addColumn("password")
    dataset.addColumn("mac_address")
    dataset.addColumn("op_station")
    dataset.addColumn("ip_address")
    dataset.addColumn("authorization_id")
    dataset.addColumn("login_time")
    dataset.addColumn("verification_code")
    dataset.appendRow()
    dataset.updateString("operator_no", OPERATOR_NO)
    dataset.updateString("password", PASSWD)
    dataset.updateString("mac_address", "123")
    dataset.updateString("op_station", "123")
    dataset.updateString("ip_address", "123")
    dataset.updateString("authorization_id", "")
    dataset.updateString("login_time", "")
    dataset.updateString("verification_code", "")
    dataset
  }

  private def _callSerivce(funcno: Int, dataset: IDataset): IDatasets = {
    val result: IDatasets = null

    val event: IEvent = ContextUtil.getServiceContext().getEventFactory()
      .getEventByAlias(String.valueOf(funcno), EventType.ET_REQUEST)
    event.putEventData(dataset)
    val rsp: IEvent = client.sendReceive(event, TIMEOUT)
    // 先判断返回值
    if (rsp.getReturnCode() != EventReturnCode.I_OK) { // 返回错误
      throw new T2SDKException(rsp.getErrorNo(), rsp.getErrorInfo())
    } else {
     val  result = rsp.getEventDatas()
    }
    return result
  }


}
