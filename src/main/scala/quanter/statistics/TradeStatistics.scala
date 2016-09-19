/**
  *
  */
package quanter.statistics

import java.util.Date

import quanter.TimeSpan

/**
  *
  */
class TradeStatistics(trades: List[Trade]) {

  def this() {
    this(null)
  }
  private var _maxConsecutiveWinners = 0
  private var _maxConsecutiveLosers = 0
  private var _maxTotalProfitLoss = 0.0
  private var _maxTotalProfitLossWithMfe = 0.0
  private var _sumForVariance = 0.0
  private var _sumForDownsideVariance = 0.0
  private var _lastPeakTime = new Date(0)
  private var _isInDrawdown = false



  val startDateTime = new Date
  val endDateTime = new Date
  val totalNumberOfTrades  = 0
  val numberOfWinningTrades = 0
  val numberOfLosingTrades = 0
  val totalProfitLoss = 0.0
  val totalProfit = 0.0
  val totalLoss =0.0
  val largestProfit = 0.0
  val largestLoss = 0.0
  val averageProfitLoss = 0.0
  val averageProfit =0.0
  val averageLoss = 0.0
  val averageTradeDuration= TimeSpan.fromDays(10)
  val averageWinningTradeDuration = TimeSpan.fromDays(10)
  val averageLosingTradeDuration = TimeSpan.fromDays(10)
  val maxConsecutiveWinningTrades = 0
  val maxConsecutiveLosingTrades  =0
  val profitLossRatio =  0.0
  val winLossRatio = 0.0
  val winRate = 0.0
  val lossRate = 0.0
  val averageMAE = 0.0
  val averageMFE = 0.0
  val largestMAE = 0.0
  val largestMFE = 0.0
  val maximumClosedTradeDrawdown = 0.0
  val maximumIntraTradeDrawdown = 0.0
  val profitLossStandardDeviation = 0.0
  val profitLossDownsideDeviation = 0.0
  val profitFactor = 0.0
  val sharpeRatio =0.0
  val sortinoRatio =0.0
  val profitToMaxDrawdownRatio =0.0
  val maximumEndTradeDrawdown =0.0
  val averageEndTradeDrawdown = 0.0
  val maximumDrawdownDuration = TimeSpan.fromDays(10)
  val totalFees = 0.0

}
