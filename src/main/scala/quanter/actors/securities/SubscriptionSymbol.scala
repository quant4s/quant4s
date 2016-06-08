package quanter.actors.securities

case class SubscriptionSymbol(symbol: String)
case class UnsubscriptionSymbol(symbol: String)
case class TickDataArrived(tickData: String)

