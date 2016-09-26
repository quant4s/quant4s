package quanter.actors

import quanter.rest._

  case class NewStrategy(strategy: Strategy) {}
  case class ListStrategies(){}
  case class UpdateStrategy(strategy: Strategy){}
  case class DeleteStrategy(id: Int){}
  case class GetStrategy(id: Int){}

  case class StartStrategy(id: Int)
  case class PauseStrategy(id: Int)
  case class RestoreStrategy(id: Int)
  case class StopStrategy(id: Int)
  case class OpenRiskControl(id: Int)
  case class CloseRiskControl(id: Int)
  case class UpdateRiskControlInfo(id: Int)
  case class UpdateTradeAccount(id: Int)
  case class AddRisk(risk: String, rule: String)



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

  case class AskListenedSymbol(symbol: String)

  case class SecuritySelection(topic: String, cmds: SecurityPicker)