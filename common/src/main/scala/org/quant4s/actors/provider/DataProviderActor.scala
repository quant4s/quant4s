/**
  *
  */
package org.quant4s.actors.provider

import akka.actor.{Actor, ActorLogging, ActorSelection, FSM}
import org.quant4s.actors.securities.SecuritiesManagerActor
import org.quant4s.securities.Security
import org.quant4s.actors.AskListenedSymbol
import org.quant4s.actors.provider.DataProviderActor._
// import quanter.actors.scheduling.ExecuteJob

import scala.collection.mutable


case class QuerySnapData()
case class Execute()

/**
  *
  */
trait DataProviderActor extends FSM[DataProviderState, DataProviderData] with ActorLogging {
  var symbolSelections = new mutable.HashMap[String, ActorSelection]
  var smRef = context.actorSelection("/user/" + SecuritiesManagerActor.path)
  var listenerRef : List[ActorSelection] = List[ActorSelection]()
  var connected = false
  var logined = false

  startWith(Initialized, new DataProviderData(Initialized))
  when(Initialized) {
    case Event(ConnectDataProvider(), _)=> {
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
      goto(Logined)
  }
  when(Logined) {
//    case Event(job: ExecuteJob, _) =>   // TODO: 定时作业
//      executeJob()
//      stay
    case Event(DisConnectedSuccess(), _) =>
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

  protected def connect(): Unit = {}
  protected def login(): Unit = {}
  protected def executeJob(): Unit = {}

  protected def addSymbol(symbol: String): Unit = {
    val sec = new Security(symbol)
    smRef ! sec
    if (!symbolSelections.contains(symbol)) {
      log.debug(s"准备接受${symbol}的行情数据")
      val ref = context.actorSelection(s"/user/${SecuritiesManagerActor.path}/${symbol}")
      symbolSelections += (symbol -> ref)
    }
  }

  def fireEvent(data: DataProviderData): Unit = {
    listenerRef.foreach( ref => ref ! data)
  }
}

object DataProviderActor {
  sealed trait DataProviderState

  case object Initialized extends DataProviderState
  case object Connected extends DataProviderState
  case object Disconnected extends DataProviderState
  case object Logined extends DataProviderState

  case class DataProviderData(state: DataProviderState)
}
