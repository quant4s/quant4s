/**
  *
  */
package quanter.statistics

/**
  *
  */
object TradeDirection extends Enumeration {
  type TradeDirection = Value
  val Long, Short = Value
}

object FillGroupingMethod extends Enumeration {
  type FillGroupingMethod = Value
  val FillToFill, FlatToFlat, FlatToReduced = Value
}

object FillMatchingMethod extends Enumeration {
  type FillMatchingMethod = Value
  val FIFO, LIFO = Value
}
