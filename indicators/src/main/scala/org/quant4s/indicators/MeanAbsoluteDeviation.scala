/**
  *
  */
package org.quant4s.indicators

import org.quant4s.indicators.window.{ReadOnlyWindow, WindowIndicator}
import org.quant4s.indicators.IndicatorExtensions._

/**
  *
  */
class MeanAbsoluteDeviation(pname: String, pperiod: Int) extends WindowIndicator[IndicatorDataPoint](pname, pperiod) {
  val mean = MovingAverageType.Simple.asIndicator("%s_%s".format(name, "Mean"), period)

  override def isReady: Boolean = samples >= period

  override def reset: Unit = {
    mean.reset
    super.reset
  }

  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = {
    implicit val num = new Numeric[IndicatorDataPoint]() {
      override def plus(x: IndicatorDataPoint, y: IndicatorDataPoint): IndicatorDataPoint = null
      override def toDouble(x: IndicatorDataPoint): Double = x.value
      override def toFloat(x: IndicatorDataPoint): Float = x.value.toFloat
      override def toInt(x: IndicatorDataPoint): Int = x.value.toInt
      override def negate(x: IndicatorDataPoint): IndicatorDataPoint = null
      override def fromInt(x: Int): IndicatorDataPoint = null
      override def toLong(x: IndicatorDataPoint): Long = x.value.toLong
      override def times(x: IndicatorDataPoint, y: IndicatorDataPoint): IndicatorDataPoint = null
      override def minus(x: IndicatorDataPoint, y: IndicatorDataPoint): IndicatorDataPoint = null
      override def compare(x: IndicatorDataPoint, y: IndicatorDataPoint): Int = x.value.compare(y.value)
    }

    mean.update(input)
    if(samples < 2) 0
    else {
      var sum = 0.0
      window.foreach(f => sum += math.abs(f.value - mean.current.value))
      sum / window.count
    }

//    if (Samples < 2)
//    {
//      return 0m;
//    }
//    return window.Average(v => Math.Abs(v - Mean.Current.Value))
  }
}
