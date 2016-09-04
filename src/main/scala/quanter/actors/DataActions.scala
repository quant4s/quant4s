package quanter.actors

import quanter.rest.{CancelOrder, Order, Strategy, Trader}

  case class NewStrategy(strategy: Strategy) {}
  case class UpdateStrategy(strategy: Strategy){}
  case class DeleteStrategy(id: Int){}
  case class GetStrategy(id: Int){}
  case class ListStrategies(){}

  case class NewOrder(order: Order){}
  case class RemoveOrder(order: CancelOrder){}

  case class NewTrader(trader: Trader){}
  case class ListTraders(){}
  case class UpdateTrader(trader: Trader){}
  case class DeleteTrader(id: Int){}
  case class GetTrader(id: Int){}

  case class KeepAlive()
  case class Connect()
  case class Disconnect()
