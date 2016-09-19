/**
  *
  */
package quanter.statistics

import java.util.Date

import org.apache.commons.math3.stat.descriptive.moment.{Mean, Variance}

import scala.collection.SortedMap
import math._
import scala.collection.mutable.ArrayBuffer
import quanter.CommonExtensions.IterableExt
import quanter.CommonExtensions.DateExt

/**
  * 资金组合统计
  */

object PortfolioStatistics {
  def compoundingAnnualPerformance(startingCapital: Double, finalCapital: Double, years: Double): Double = {
    if(years == 0)  0.0 else math.pow(finalCapital / startingCapital, 1 / years) - 1
  }

  def  drawdownPercent(equityOverTime: SortedMap[Date, Double] , rounding: Int = 2): Double = {
    var ret = 0.0
    val prices = equityOverTime.values.toArray
    if (prices.length != 0) {
      var drawdowns = new ArrayBuffer[Double]()
      var high = prices(0)
      for( price <- prices) {
        if (price > high) high = price;
        if (high > 0) drawdowns += (price / high - 1)
      }
      ret = round(abs(drawdowns.min) * pow(10,rounding)) / pow(10,rounding)
    }
    ret
  }

  def  getAnnualVariance(performance: List[Double], tradingDaysPerYear: Int = 252): Double = {
    performance.variance(tradingDaysPerYear)
  }


  def getAnnualPerformance(performance: List[Double], tradingDaysPerYear: Int = 252) : Double = {
    val mean = new Mean()
    mean.evaluate(performance.toArray) * tradingDaysPerYear
  }



}

class PortfolioStatistics(profitLoss: SortedMap[Date, Double],
                          equity: SortedMap[Date, Double],
                          listPerformance: List[Double],
                          listBenchmark: List[Double],
                          startingCapital: Double,
                          tradingDaysPerYear: Int = 252) {
  def this() {
    this(null, null, null, null, 0.0)
  }
  val riskFreeRate = 0.0

  var runningCapital = startingCapital
  var totalProfit = 0.0
  var totalLoss = 0.0
  var totalWins = 0
  var totalLosses = 0

  for(pair <- profitLoss) {
    val tradeProfitLoss = pair._2
    if (tradeProfitLoss > 0) {
      totalProfit += tradeProfitLoss / runningCapital
      totalWins += 1
    } else {
      totalLoss += tradeProfitLoss / runningCapital
      totalLosses += 1
    }
    runningCapital += tradeProfitLoss;
  }

  val averageWinRate = if(totalWins == 0) 0 else totalProfit / totalWins
  val averageLossRate = if(totalLosses == 0) 0 else totalLoss / totalLosses
  val profitLossRatio = if(averageLossRate == 0) 0 else averageWinRate / math.abs(averageLossRate)

  val winRate = if(profitLoss.size == 0)  0 else totalWins / profitLoss.size
  val lossRate = if(profitLoss.size == 0) 0 else totalLosses / profitLoss.size
  val expectancy = winRate * profitLossRatio - lossRate

  val totalNetProfit = if (profitLoss.size > 0) 0 /*equity.Values.LastOrDefault() / startingCapital) - 1*/ else 0

  private val _fractionOfYears =   (equity.lastKey - equity.firstKey).totalDays / 365
  val compoundingAnnualReturn: Double = PortfolioStatistics.compoundingAnnualPerformance(startingCapital, equity(equity.lastKey), _fractionOfYears)

  val drawdown = PortfolioStatistics.drawdownPercent(equity, 3)

  val annualVariance = PortfolioStatistics.getAnnualVariance(listPerformance, tradingDaysPerYear)
  val annualStandardDeviation = sqrt(annualVariance)

  val annualPerformance = PortfolioStatistics.getAnnualPerformance(listPerformance, tradingDaysPerYear)
  val sharpeRatio = if(annualStandardDeviation == 0)  0 else (annualPerformance - riskFreeRate) / annualStandardDeviation

  val benchmarkVariance = listBenchmark.variance()
  val beta = if(benchmarkVariance == 0) 0 else listPerformance.covariance(listBenchmark) / benchmarkVariance

  val alpha = if(beta == 0) 0 else annualPerformance - (riskFreeRate + beta * (PortfolioStatistics.getAnnualPerformance(listBenchmark, tradingDaysPerYear) - riskFreeRate));

  private val _correlation = listPerformance.pearsonsCorrelation(listBenchmark)
  private val _benchmarkAnnualVariance = benchmarkVariance * tradingDaysPerYear
  val trackingError = if((_correlation == 0.0) || (_benchmarkAnnualVariance == 0.0)) 0 else
    sqrt(annualVariance - 2 * _correlation * annualStandardDeviation * sqrt(_benchmarkAnnualVariance) + _benchmarkAnnualVariance)

  val informationRatio = if(trackingError == 0)  0 else (annualPerformance - PortfolioStatistics.getAnnualPerformance(listBenchmark, tradingDaysPerYear)) / trackingError
  val treynorRatio = if(beta == 0) 0 else (annualPerformance - riskFreeRate) / beta;

}
