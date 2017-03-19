/**
  *
  */
package quanter.statistics

import java.util.Date

import quanter.TimeSpan
import quanter.statistics.TradeDirection.TradeDirection
import quanter.CommonExtensions._

/**
  *
  */
class Trade(val symbol: String, val entryTime: Date = null, val entryPrice: Double = 0.0,
            val direction: TradeDirection = TradeDirection.Long, var quantity: Int = 0,
            val exitTime: Date = null, val exitPrice: Double = 0.0, val totalFees: Double = 0.0,
            val MFE: Double = 0.0, val MAE: Double = 0.0, val profitLoss: Double = 0.0) {
  def duration = exitTime - entryTime
  def endTradeDrawdown = profitLoss - MFE
}
