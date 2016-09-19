/**
  *
  */
package quanter.statistics

import java.util.Date

import scala.collection.{SortedMap, mutable}

/**
  * TradeStatistics 和 PortfolioStatistics 的包装器
  */
class AlgorithmPerformance(trades: List[Trade], profitLoss: SortedMap[Date, Double],
                           equity: SortedMap[Date, Double], listPerformance: List[Double],
                           listBenchmark: List[Double], startingCapital: Double) {

  val tradeStatistics = if(trades != null) new TradeStatistics(trades) else new TradeStatistics()
  val portfolioStatistics = if(profitLoss != null) new PortfolioStatistics(profitLoss, equity, listPerformance, listBenchmark, startingCapital) else new PortfolioStatistics()
  val closedTrades = if(trades != null) trades else new mutable.LinkedList[Trade]()

  def this() {
    this(null, null, null, null, null, 0.0)
  }


}
