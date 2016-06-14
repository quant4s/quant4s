/**
  *
  */
package quanter.statistics

import java.util.Date

import scala.collection.immutable.SortedMap

/**
  *
  */
class StrategyPerformance(trades: List[Trade],
                          profitLoss: SortedMap[Date, Double],
                          equity: SortedMap[Date, Double],
                          listPerformance: List[Double],
                          listBenchmark: List[Double],
                          startingCapital: Double) {

  val tradeStatistics: TradeStatistics = new TradeStatistics(trades)
  val portfolioStatistics: PortfolioStatistics = new PortfolioStatistics(profitLoss, equity, listPerformance, listBenchmark, startingCapital)
  val closedTrades = trades
}
