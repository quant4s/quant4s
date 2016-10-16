/**
  *
  */
package quanter.actors.provider

import akka.actor.{Actor, ActorLogging, ActorSelection, FSM}
import quanter.actors.AskListenedSymbol
import quanter.actors.provider.DataProviderActor._
import quanter.actors.scheduling.ExecuteJob
import quanter.actors.securities.SecuritiesManagerActor

import scala.collection.mutable


case class QuerySnapData()
case class Execute()

/**
  *
  */
abstract class DataProviderActor extends FSM[DataProviderState, DataProviderData] with ActorLogging {
  var symbolSelections = new mutable.HashMap[String, ActorSelection]
  var connected = false
  var logined = false

  startWith(Initialized, new DataProviderData())
  when(Initialized) {
    case Event(ConnectDataProvider(), _)=> {
      log.debug("开始连接数据源")
      if(connect()) goto(Connected)
      else stay()
    }
  }
  when(Connected) {
    case Event(job: ExecuteJob, _) =>   // 定时作业
      executeJob()
      stay
  }
  when(Disconnected) {
    case Event(Connected, _) =>
      goto(Connected)
  }
  whenUnhandled  {
    case Event(ask: AskListenedSymbol, _) => addSymbol(ask.symbol)
      stay

    case Event(_, _) =>
      stay
  }

  protected def connect(): Boolean = true
  protected def executeJob(): Unit = {}

  protected def addSymbol(symbol: String): Unit = {
    if (!symbolSelections.contains(symbol)) {
      log.debug(s"准备接受${symbol}的行情数据")
      val ref = context.actorSelection(s"/user/${SecuritiesManagerActor.path}/${symbol}")
      symbolSelections += (symbol -> ref)
    }
  }
}

object DataProviderActor {
  sealed trait DataProviderState

  case object Initialized extends DataProviderState
  case object Connected extends DataProviderState
  case object Disconnected extends DataProviderState
  case object Login extends DataProviderState

  case class DataProviderData()
}
