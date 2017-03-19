package org.quant4s.actors

import org.quant4s.rest._

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
  case class UpdateOrderStatus(id: Int, status: String)

  case class NewTrader(trader: TradeAccount){}
  case class ListTraders(){}
  case class UpdateTrader(trader: TradeAccount){}
  case class DeleteTrader(id: Int){}
  case class GetTrader(id: Int){}

  case class Connect()
  case class ConnectedSuccess()
  case class LoginSuccess()
  case class KeepAlive()
  case class Disconnect()

  case class AskListenedSymbol(symbol: String)

  case class SecuritySelection(topic: String, cmds: SecurityPicker)