package org.quant4s.indicators

import java.util.Date

import org.quant4s.data.BaseData
import org.quant4s.indicators.MovingAverageType.MovingAverageType
import org.quant4s.indicators.composite.CompositeIndicator
import org.quant4s.indicators.window._

/**
  *
  */
object IndicatorExtensions {
  implicit class IndicatorBaseExt(indicator: IndicatorBase[IndicatorDataPoint]) {
    def update(time: Date, value:Double) = indicator.update(new IndicatorDataPoint(time, value))
    def update(time: Date, indicator: IndicatorBase[IndicatorDataPoint]) {update(time, indicator.current.value)}
    def minus(right: IndicatorBase[IndicatorDataPoint], name: String) = new CompositeIndicator[IndicatorDataPoint](name, indicator, right, (l, r) => l - r)
    def plus(right: IndicatorBase[IndicatorDataPoint], name: String) =  new CompositeIndicator[IndicatorDataPoint](name, indicator, right, (l, r) => l + r)
    def times(constant: Double): CompositeIndicator[IndicatorDataPoint] = {
      val constantIndicator = new ConstantIndicator[IndicatorDataPoint](constant.toString(), constant)
      indicator.times(constantIndicator)
    }

    def times(right: IndicatorBase[IndicatorDataPoint]): CompositeIndicator[IndicatorDataPoint] = {
      new CompositeIndicator[IndicatorDataPoint](indicator, right, (l, r) => l * r)
    }
  }

//  implicit class IndicatorBaseExt2[T](indicator: IndicatorBase[T]) {
//    def weightBy[TWeight](weight: TWeight, period: Int) = {
//
//    }
//  }

  implicit class BaseDataExt(data: BaseData) {
    def +(right: BaseData) = data.value + right.value
    def +(right: Double) = data.value + right
    def -(right: BaseData) = data.value - right.value
    def -(right: Double) = data.value - right
    def *(right: BaseData) = data.value * right.value
    def *(right: Double) = data.value * right
    def /(right: BaseData) = data.value - right.value
    def /(right: Double) = data.value - right
  }

  implicit class MovingAverageTypeExt(movingAverageType: MovingAverageType) {
    def asIndicator(name: String, period: Int): IndicatorBase[IndicatorDataPoint] = {
      movingAverageType match {
        case MovingAverageType.Simple => new SimpleMovingAverage(name, period)
        case MovingAverageType.Wilders => new ExponentialMovingAverage(name, period, 1 / period)
        case MovingAverageType.Exponential => new ExponentialMovingAverage(name, period)
        case MovingAverageType.LinearWeightedMovingAverage => new LinearWeightedMovingAverage(name, period)
        case MovingAverageType.DoubleExponential => new DoubleExponentialMovingAverage(name, period)
        case MovingAverageType.TripleExponential => new TripleExponentialMovingAverage(name, period)
        case MovingAverageType.Triangular => new TriangularMovingAverage(name, period)
        case MovingAverageType.T3 => new T3MovingAverage(name, period)
      }
    }
  }

}
