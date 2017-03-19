/**
  *
  */
package quanter.actors.trade.ustp

import com.sun.jna.{Callback, Library, Native}
import quanter.actors.trade.{BrokerageActor, CallBack}
import quanter.rest.{CancelOrder, Order}

/**
  *
  */
class USTPBrokerageActor extends BrokerageActor with CallBack {



  override def login(): Unit = {

  }


  def initTrader() {
    val path = "./logs/ustp/trader/"
    CLibrary.INSTANCE.init(accountInfo.brokerUri, path, accountInfo.brokerAccount, this)
  }

  override def messageReceived(data: String): Unit = {
    log.info("FromC:" + data)
    val msgArr = data.split("|")
    val first = msgArr(0).toInt

    first match {
      case 7 => processCounterStatusMessage(msgArr)
      case 1051 => processAccountLoginResultMessage(msgArr)
    }
  }

  def isError( arr: Array[String]) = {
    "err".equalsIgnoreCase(arr.takeRight(1)(0))
  }
  def processCounterStatusMessage(arr: Array[String]): Unit = {
    if (arr(2).equals("0")) {
      this.connectNum += 1
      this._isConnected = true
    } else {
      this._isConnected = false
    }
  }

  def processAccountLoginResultMessage(arr: Array[String]): Unit = {
    if(isError(arr)) {
      log.info("USTP 登录失败，失败代码：%s, 原因：%s".format(arr(3),arr(4)))
      _isLogin = false
    }
    else {
      log.info("USTP 登录成功")
      _isLogin = true
    }
  }



  trait CLibrary extends Library {
    def init(url: String, path: String, account: String, callback: Callback)
    def release(paramString: String)
  }

  object CLibrary {
   val INSTANCE = Native.loadLibrary("", classOf[CLibrary]).asInstanceOf[CLibrary]
  }

  // Trader implemention
  override def queryCapital(): Unit = ???

  override def queryOrders(): Unit = ???

  override def cancel(order: CancelOrder): Unit = ???

  override def order(order: Order): Unit = ???

  override def queryPosition(symbol: String): Unit = ???

  override def queryUnfinishOrders(): Unit = ???
}
