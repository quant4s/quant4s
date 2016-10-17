package quanter.actors

import quanter.actors.trade.TradeAccountEvent.TradeAccountEvent

/**
  * 1. 连接成功
  * 2. 连接失败
  * 3. 登录成功
  * 4. 登录失败
  * 5. 断开连接
  */
package object trade {
  object TradeAccountEvent extends Enumeration {
    type TradeAccountEvent = Value
    val Connected_Success, Connected_Failure, Logined_Success, Logined_Failure, Disconnected = Value
  }

  case class TradeAccountMessage(event: TradeAccountEvent, accountId: Int)

  case class LoginResult(id: Int, status: String)


}
