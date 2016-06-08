package quanter.indicators

import java.util.Date

import quanter.data.BaseData

/**
  *
  */
class ConstantIndicator[T <: BaseData](pname: String, private val pvalue: Double) extends IndicatorBase[T](pname){

  current =  new IndicatorDataPoint(new Date(0), pvalue)
  override def isReady: Boolean = true
  override def computeNextValue(input: T): Double = pvalue

  override def reset: Unit = {
    super.reset
    current = new IndicatorDataPoint(new Date(0), pvalue)
  }
}
