/**
  *
  */
package quanter.statistics

import java.util.Date

import quanter.TimeSpan
import quanter.CommonExtensions._

/**
  *
  */
class TradeStatistics(trades: List[Trade]) {

  def this() {
    this(List[Trade]())
  }

  private var maxConsecutiveWinners = 0
  private var maxConsecutiveLosers = 0
  private var maxTotalProfitLoss = 0.0
  private var maxTotalProfitLossWithMfe = 0.0
  private var sumForVariance = 0.0
  private var sumForDownsideVariance = 0.0
  private var lastPeakTime = new Date(0)
  private var isInDrawdown = false



  var startDateTime: Date = null
  var endDateTime: Date = null
  var totalNumberOfTrades  = 0
  var numberOfWinningTrades = 0
  var numberOfLosingTrades = 0
  var totalProfitLoss = 0.0
  var totalProfit = 0.0
  var totalLoss =0.0
  var largestProfit = 0.0
  var largestLoss = 0.0
  var averageProfitLoss = 0.0
  var averageProfit =0.0
  var averageLoss = 0.0
  var averageTradeDuration= TimeSpan.zero
  var averageWinningTradeDuration = TimeSpan.zero
  var averageLosingTradeDuration = TimeSpan.zero
  var maxConsecutiveWinningTrades = 0
  var maxConsecutiveLosingTrades  =0
  var profitLossRatio =  0.0
  var winLossRatio = 0.0
  var winRate = 0.0
  var lossRate = 0.0
  var averageMAE = 0.0
  var averageMFE = 0.0
  var largestMAE = 0.0
  var largestMFE = 0.0
  var maximumClosedTradeDrawdown = 0.0
  var maximumIntraTradeDrawdown = 0.0
  var profitLossStandardDeviation = 0.0
  var profitLossDownsideDeviation = 0.0
  var profitFactor = 0.0
  var sharpeRatio =0.0
  var sortinoRatio =0.0
  var profitToMaxDrawdownRatio =0.0
  var maximumEndTradeDrawdown =0.0
  var averageEndTradeDrawdown = 0.0
  var maximumDrawdownDuration = TimeSpan.zero
  var totalFees = 0.0

  for(trade <- trades) {
    if (lastPeakTime == new Date(0)) lastPeakTime = trade.entryTime

    startDateTime = if (startDateTime == null || trade.entryTime.after(startDateTime)) trade.entryTime else null
    endDateTime = if (endDateTime == null || trade.exitTime.after(endDateTime)) trade.exitTime else null

    totalNumberOfTrades += 1
    maxTotalProfitLossWithMfe = if (totalProfitLoss + trade.MFE > maxTotalProfitLossWithMfe) totalProfitLoss + trade.MFE else maxTotalProfitLossWithMfe
    maximumIntraTradeDrawdown = if (totalProfitLoss + trade.MAE - maxTotalProfitLossWithMfe < maximumIntraTradeDrawdown)  totalProfitLoss + trade.MAE - maxTotalProfitLossWithMfe else maximumIntraTradeDrawdown

    if(trade.profitLoss > 0) {
      numberOfWinningTrades += 1
      totalProfitLoss += trade.profitLoss
      totalProfit += trade.profitLoss
      averageProfit += (trade.profitLoss - averageProfit) / numberOfWinningTrades
      averageWinningTradeDuration = averageWinningTradeDuration + (trade.duration - averageWinningTradeDuration) / numberOfWinningTrades
      if (trade.profitLoss > largestProfit) largestProfit = trade.profitLoss
      maxConsecutiveWinners += 1
      maxConsecutiveLosers = 0
      if (maxConsecutiveWinners > maxConsecutiveWinningTrades) maxConsecutiveWinningTrades = maxConsecutiveWinners
      if (totalProfitLoss > maxTotalProfitLoss) {
        maxTotalProfitLoss = totalProfitLoss
        if (isInDrawdown && ((trade.exitTime - lastPeakTime) > maximumDrawdownDuration)) maximumDrawdownDuration = trade.exitTime - lastPeakTime
        lastPeakTime = trade.exitTime
        isInDrawdown = false
      }
    } else {
      numberOfLosingTrades += 1
      totalProfitLoss += trade.profitLoss
      totalLoss += trade.profitLoss
      val prevAverageLoss = averageLoss
      averageLoss += (trade.profitLoss - averageLoss) / numberOfLosingTrades

      sumForDownsideVariance += (trade.profitLoss - prevAverageLoss) * (trade.profitLoss - averageLoss)
      val downsideVariance = if(numberOfLosingTrades > 1) sumForDownsideVariance / (numberOfLosingTrades - 1) else 0.0
      profitLossDownsideDeviation = math.sqrt(downsideVariance)

      averageLosingTradeDuration = averageLosingTradeDuration + (trade.duration - averageLosingTradeDuration) / numberOfLosingTrades

      if (trade.profitLoss < largestLoss) largestLoss = trade.profitLoss

      maxConsecutiveWinners = 0
      maxConsecutiveLosers += 1
      if (maxConsecutiveLosers > maxConsecutiveLosingTrades) maxConsecutiveLosingTrades = maxConsecutiveLosers

      if (totalProfitLoss - maxTotalProfitLoss < maximumClosedTradeDrawdown) maximumClosedTradeDrawdown = totalProfitLoss - maxTotalProfitLoss

      isInDrawdown = true
    }
    val prevAverageProfitLoss = averageProfitLoss
    averageProfitLoss += (trade.profitLoss - averageProfitLoss) / totalNumberOfTrades

    sumForVariance += (trade.profitLoss - prevAverageProfitLoss) * (trade.profitLoss - averageProfitLoss)
    val variance = if(totalNumberOfTrades > 1) sumForVariance / (totalNumberOfTrades - 1) else 0.0
    profitLossStandardDeviation = math.sqrt(variance)

    averageTradeDuration = averageTradeDuration + (trade.duration - averageTradeDuration) / totalNumberOfTrades
    averageMAE += (trade.MAE - averageMAE) / totalNumberOfTrades
    averageMFE += (trade.MFE - averageMFE) / totalNumberOfTrades

    if (trade.MAE < largestMAE) largestMAE = trade.MAE
    if (trade.MFE > largestMFE) largestMFE = trade.MFE

    if (trade.endTradeDrawdown < maximumEndTradeDrawdown)
      maximumEndTradeDrawdown = trade.endTradeDrawdown

    totalFees += trade.totalFees
  }

  profitLossRatio = if(averageLoss == 0) 0 else averageProfit / math.abs(averageLoss)
  winLossRatio = if(totalNumberOfTrades == 0) 0 else if(numberOfLosingTrades > 0) numberOfWinningTrades / numberOfLosingTrades else 10
  winRate = if(totalNumberOfTrades > 0) numberOfWinningTrades / totalNumberOfTrades else 0
  lossRate = if(totalNumberOfTrades > 0)  1 - winRate else 0
  profitFactor = if(totalProfit == 0) 0 else if (totalLoss < 0) totalProfit / math.abs(totalLoss) else 10
  sharpeRatio = if(profitLossStandardDeviation > 0) averageProfitLoss / profitLossStandardDeviation else 0
  sortinoRatio = if(profitLossDownsideDeviation > 0) averageProfitLoss / profitLossDownsideDeviation else 0
  profitToMaxDrawdownRatio = if(totalProfitLoss == 0) 0 else if(maximumClosedTradeDrawdown < 0) totalProfitLoss / math.abs(maximumClosedTradeDrawdown) else 10

  averageEndTradeDrawdown = averageProfitLoss - averageMFE


}
