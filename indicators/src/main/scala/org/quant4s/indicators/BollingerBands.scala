package org.quant4s.indicators

import org.quant4s.indicators.IndicatorExtensions._
import org.quant4s.indicators.MovingAverageType.MovingAverageType

/**
  *
  */
class BollingerBands(pname: String, pperiod: Int, pk: Double, pmovingAverageType: MovingAverageType = MovingAverageType.Simple) extends Indicator(pname){
  private val _movingAverageType = pmovingAverageType
  private val _standardDeviation = new StandardDeviation(name + "_StandardDeviation", pperiod);
  private val _middleBand = _movingAverageType.asIndicator("%s_middleBank".format(pname), pperiod)
  private val _lowerBand = _middleBand.minus(_standardDeviation.times(pk), name+"_lowerBand")
  private val _upperBand = _middleBand.plus(_standardDeviation.times(pk), name+"_upperBand")

  def this(pperiod: Int, pk: Double) {
    this("BB(%s,%d)".format(pperiod, pk), pperiod, pk, MovingAverageType.Simple)
  }
  def this(pperiod: Int, pk: Double, pmovingAverageType: MovingAverageType) {
    this("BB(%s,%d)".format(pperiod, pk), pperiod, pk, pmovingAverageType)
  }


  override def isReady: Boolean = _middleBand.isReady && _upperBand.isReady && _lowerBand.isReady

  override def computeNextValue(input: IndicatorDataPoint): Double = {
    _standardDeviation.update(input)
    _middleBand.update(input)
    _lowerBand.update(input)
    _upperBand.update(input)

    input.value
  }

  override def toJson = ""
}
