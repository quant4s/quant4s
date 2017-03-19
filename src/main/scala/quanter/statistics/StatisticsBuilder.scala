package quanter.statistics

import java.util.Date

import scala.collection.immutable.SortedMap
import quanter.CommonExtensions.DateExt

/**
  *
  */
object StatisticsBuilder {
  def getStrategyPerformance(fromDate: Date,
                             toDate: Date,
                             trades: List[Trade],
                             profitLoss: SortedMap[Date, Double],
                             equity: SortedMap[Date, Double],
                             pointsPerformance: List[ChartPoint],
                             pointsBenchmark: List[ChartPoint],
                             startingCapital: Double): StrategyPerformance = {
    val periodTrades = trades.filter( t => t.exitTime >= fromDate && t.exitTime <= toDate.addDays(1)).toList
    val periodProfitLoss = profitLoss.filterKeys( p => p >= fromDate && p <= toDate.addDays(1))
    val periodEquity = equity.filterKeys( p => p >= fromDate && p <= toDate.addDays(1))

    val _performance = _chartPointToDictionary(pointsPerformance, fromDate, toDate)
    val listPerformance: List[Double] = _performance.values.toList.map(f => f / 100 )


    val _benchmark = _chartPointToDictionary(pointsBenchmark, fromDate, toDate)
    val listBenchmark = _createBenchmarkDifferences(_benchmark, periodEquity)
    _ensureSameLength(listPerformance, listBenchmark)

    val runningCapital = if(equity.size == periodEquity.size)  startingCapital else periodEquity(periodEquity.firstKey)

    new StrategyPerformance(periodTrades, periodProfitLoss, periodEquity, listPerformance, listBenchmark, runningCapital)
  }

  private def _chartPointToDictionary(points: Iterable[ChartPoint], fromDate: Date = null, toDate: Date = null): SortedMap[Date, Double] = {
   null
  }

  private def _createBenchmarkDifferences(benchmark: SortedMap[Date, Double] , equity: SortedMap[Date, Double]):  List[Double] = {
   null
  }

  private def _ensureSameLength(listPerformance:  List[Double], listBenchmark:  List[Double]): Unit = {
//    while (listPerformance.size < listBenchmark.size)
//    {
//      listPerformance = listPerformance :: 0
//    }
//    while (listPerformance.size > listBenchmark.size)
//    {
//      listBenchmark = listBenchmark :: 0
//    }
  }

}
