/**
  *
  */
package org.quant4s.actors

import akka.actor.{ActorLogging, FSM}
import org.quant4s.actors.ConnectorFSM.{LoginSuccess, _}

/**
  *
  */
trait ConnectorFSM extends FSM[DataProviderState, DataProviderData] with ActorLogging{

  startWith(Initialized, new DataProviderData(Initialized))

  when(Initialized) {
    case Event(AskConnect(), _)=> {
      log.debug("开始连接数据源")
      connect
      stay
    }
    case Event(ConnectedSuccess(), data) => {
      log.debug("连接成功，准备登录")
      login()
      // TODO: 通知监听者，连接成功事件
      fireEvent(data)
      goto(Connected) using data.copy(state = Connected)
    }
  }
  when(Connected) {
    case Event(LoginSuccess(), _) =>
      log.debug("登录成功")
      goto(LoggedIn)
  }
  when(LoggedIn) {
    case Event(job: ExecuteJob, _) =>   // 定时作业
      executeJob()
      stay
    case Event(DisconnectSuccess(), _) =>
      log.debug("断开连接")
      goto(Disconnected)
  }
  when(Disconnected) {
    case Event(ConnectedSuccess(), _) =>{
      log.debug("连接成功，准备登录")
      login()
      goto(Connected)
    }
  }
  whenUnhandled  {
    case Event(ask: AskListenedSymbol, _) => addSymbol(ask.symbol)
      stay
    case Event(_, _) =>
      stay
  }

  protected def connect(): Unit = { self ! new ConnectedSuccess() }
  protected def login(): Unit = { self ! new LoginSuccess()}
  protected def executeJob(): Unit = {}
  protected def addSymbol(symbol: String): Unit = {}
  protected def fireEvent(data: DataProviderData) = {}

}

object ConnectorFSM {
  sealed trait DataProviderState

  case object Initialized extends DataProviderState
  case object Connected extends DataProviderState
  case object Disconnected extends DataProviderState
  case object LoggedIn extends DataProviderState

  case class DataProviderData(state: DataProviderState)

  case class AskConnect()
  case class ConnectSuccess()
  case class ConnectFailure()
  case class AskLogin()
  case class LoginSuccess()
  case class LoginFailure()
  case class AskDisconnect()
  case class DisconnectSuccess()
  case class DisconnectFailure()
  case class ExecuteJob()
}