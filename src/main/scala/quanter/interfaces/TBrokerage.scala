package quanter.interfaces

import quanter.rest.Trader

/**
  *
  */
trait TBrokerage {
  var name: String
  var accountInfo: Trader

  def isConnected: Boolean

  def connect
  def disconnect
  def keep

  def buy(code: String, price: Double, quantity: Int)
  def sell(code: String, price: Double, quantity: Int)
  // def fetchHolding(): Array
  // def fetchPortolio
}

