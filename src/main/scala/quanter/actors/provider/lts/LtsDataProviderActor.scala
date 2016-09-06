/**
  *
  */
package quanter.actors.provider.lts

import quanter.actors.AskListenedSymbol
import quanter.actors.provider.{DataProviderActor, Execute, QuerySnapData}

/**
  *
  */
class LtsDataProviderActor extends DataProviderActor {
  override def receive: Receive =  {
    case ask: AskListenedSymbol => addSymbol(ask.symbol)
    case query: QuerySnapData =>
    case Execute =>
    case _ =>
  }
}
