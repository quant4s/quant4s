/**
  *
  */
package quanter.actors.provider

import akka.actor.{Actor, ActorLogging, ActorSelection}
import quanter.actors.securities.SecuritiesManagerActor

import scala.collection.mutable


case class QuerySnapData()
case class Execute()

/**
  *
  */
abstract class DataProviderActor extends Actor with ActorLogging {
  var symbolSelections = new mutable.HashMap[String, ActorSelection]

  protected def addSymbol(symbol: String): Unit = {
    if (!symbolSelections.contains(symbol)) {
      log.debug(s"准备接受${symbol}的行情数据")
      val ref = context.actorSelection(s"/user/${SecuritiesManagerActor.path}/${symbol}")
      symbolSelections += (symbol -> ref)
    }
  }
}
