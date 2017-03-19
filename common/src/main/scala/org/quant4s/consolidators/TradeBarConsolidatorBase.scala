package org.quant4s.consolidators

import org.quant4s.TimeSpan
import org.quant4s.data.TBaseData
import org.quant4s.data.TBaseData
import org.quant4s.data.market.TradeBar

/**
  *
  */
abstract class TradeBarConsolidatorBase[T <: TBaseData](pmaxCount: Option[Int] = None, ptimespan: Option[TimeSpan] = None) extends PeriodCountConsolidatorBase[T, TradeBar](pmaxCount, ptimespan) {

  def  workingBar: TradeBar = workingData.asInstanceOf[TradeBar]
}
