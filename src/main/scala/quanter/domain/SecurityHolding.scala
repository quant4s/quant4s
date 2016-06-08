package quanter.domain

/**
  *
  */
class SecurityHolding {
  var amount = 1000
  var lastPrice = 10.2
  var totalCost = 13000

  def averageCost = totalCost / amount
  def marketValue = amount * lastPrice
}
