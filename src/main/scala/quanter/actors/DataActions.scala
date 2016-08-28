package quanter.actors

import quanter.rest.{Order, Strategy, Trader}


/**
  *
  */
  case class NewStrategy(strategy: Strategy) {}
  case class UpdateStrategy(strategy: Strategy){}
  case class DeleteStrategy(id: Int){}
  case class GetStrategy(id: Int){}
  case class ListStrategies(){}

  case class NewOrder(order: Order){}
  case class CancelOrder(id: Int){}

  case class NewTrader(trader: Trader){}
  case class ListTraders(){}
  case class UpdateTrader(trader: Trader){}
  case class DeleteTrader(id: Int){}
  case class GetTrader(id: Int){}

