package org.quant4s.actors.securities

case class SubscriptionSymbol(symbol: String)
case class UnsubscriptionSymbol(symbol: String)
case class TickDataArrived(tickData: String)

