/**
  *
  */
package quanter.brokerages.hensun.t2

import com.hundsun.mcapi.MCServers
import com.hundsun.mcapi.interfaces.ISubscriber
import com.hundsun.t2sdk.common.core.context.ContextUtil
import com.hundsun.t2sdk.common.share.dataset.DatasetService
import com.hundsun.t2sdk.impl.client.T2Services
import com.hundsun.t2sdk.interfaces.{IClient, T2SDKException}
import com.hundsun.t2sdk.interfaces.share.dataset.{IDataset, IDatasets}
import com.hundsun.t2sdk.interfaces.share.event.{EventReturnCode, EventType, IEvent}
import quanter.brokerages.Brokerage

class T2Brokerage(pname: String) extends Brokerage(pname){
  def this() {
    this("")
  }
  val server = T2Services.getInstance()
  val subscriber: ISubscriber = null
  var subscribeid: Int = 0
  var client: IClient = null
  val TIMEOUT = 10000

  override def isConnected: Boolean = ???

  override def buy(code: String, price: Double, quantity: Int): Unit = ???

  override def sell(code: String, price: Double, quantity: Int): Unit = ???

  override def connect: Unit = {
    server.init()
    server.start()

    client = server.getClient("as_ufx")
  }

  override def disconnect: Unit = {
    val ret = subscriber.CancelSubscribeTopic(subscribeid)
    if (ret < 0) {
      throw new Exception("取消订阅主题失败 ret[" + ret + "]")
    }
    MCServers.Destroy()
    server.stop()
  }

  override def keep: Unit = ???

  private def _login: Unit = {
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
