package quanter.interfaces

import quanter.rest.TradeAccount

/**
  *
  */
trait TBrokerage {
  var name: String
  var accountInfo: TradeAccount = null

  protected var _isConnected = false
  def isConnected = _isConnected

  protected var _logined = false
  def isLogined = _isConnected

  def connect
  def disconnect
  def keep

  def buy(code: String, price: Double, quantity: Int)
  def sell(code: String, price: Double, quantity: Int)
  // def fetchHolding(): Array
  // def fetchPortolio
}

