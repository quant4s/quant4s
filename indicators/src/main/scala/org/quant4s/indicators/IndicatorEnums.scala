package org.quant4s.indicators

/**
  *
  */
object MovingAverageType extends Enumeration {
  type MovingAverageType = Value
  val Simple, Exponential, Wilders, LinearWeightedMovingAverage, DoubleExponential, TripleExponential, Triangular, T3 = Value

}

object IndicatorStatus extends Enumeration {
  type IndicatorStatus = Value
  val Success, InvalidInput, MathError = Value
}
