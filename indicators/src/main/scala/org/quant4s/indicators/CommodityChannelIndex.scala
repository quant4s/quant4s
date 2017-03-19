/**
  *
  */
package org.quant4s.indicators

import org.quant4s.data.market.TradeBar
import org.quant4s.indicators.MovingAverageType.MovingAverageType
import org.quant4s.indicators.IndicatorExtensions._

/**
  *
  */
class CommodityChannelIndex(pname: String, pperiod: Int, pmovingAverageType: MovingAverageType) extends TradeBarIndicator(pname){
    def this(name: String, period: Int) {
      this(name, period, MovingAverageType.Simple)
    }

  def this(period: Int, movingAverageType: MovingAverageType) {
    this("", period, movingAverageType)
  }

  def this(period: Int) {
    this(period, MovingAverageType.Simple)
  }

  val _k = 0.015

  val movingAverageType = pmovingAverageType
  val typicalPriceAverage = movingAverageType.asIndicator(name + "_TypicalPriceAvg", pperiod)
  val typicalPriceMeanDeviation = new MeanAbsoluteDeviation(name + "_TypicalPriceMAD", pperiod)

  override def isReady: Boolean = typicalPriceAverage.isReady && typicalPriceMeanDeviation.isReady

  override def reset: Unit = {
    typicalPriceAverage.reset
    typicalPriceMeanDeviation.reset
    super.reset
  }

  override def computeNextValue(input: TradeBar): Double = {
    val typicalPrice = (input.high + input.low + input.close)/3.0

    typicalPriceAverage.update(input.time, typicalPrice)
    typicalPriceMeanDeviation.update(input.time, typicalPrice)

    val weightedMeanDeviation = _k * typicalPriceMeanDeviation.current.value
    if (weightedMeanDeviation == 0.0) 0
    else (typicalPrice - typicalPriceAverage.current.value)/weightedMeanDeviation
  }
}
