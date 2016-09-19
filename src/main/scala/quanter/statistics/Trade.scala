/**
  *
  */
package quanter.statistics

import java.util.Date

import quanter.statistics.TradeDirection.TradeDirection

/**
  *
  */
class Trade {
  var symbol: String = ""
  var entryTime: Date = null
  var entryPrice = 0.0
  var Direction: TradeDirection = TradeDirection.Long
  var exitDate: Date = null
}
