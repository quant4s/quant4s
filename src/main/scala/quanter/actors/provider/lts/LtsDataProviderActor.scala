/**
  *
  */
package quanter.actors.provider.lts

import quanter.actors.provider.{AskListenedSymbol, DataProviderActor, Execute, QuerySnapData}

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
