package quanter.interfaces

/**
  *
  */
trait TBrokerage {
  def name: String
  def isConnected: Boolean

  def connect
  def disconnect
  def keep

  def buy(code: String, price: Double, quantity: Int)
  def sell(code: String, price: Double, quantity: Int)
  // def fetchHolding(): Array
  // def fetchPortolio
}

