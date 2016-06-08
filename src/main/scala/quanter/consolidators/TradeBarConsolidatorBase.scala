package quanter.consolidators

import quanter.TimeSpan
import quanter.data.{BaseData, TBaseData}
import quanter.data.market.TradeBar

/**
  *
  */
abstract class TradeBarConsolidatorBase[T <: TBaseData](pmaxCount: Option[Int] = None, ptimespan: Option[TimeSpan] = None) extends PeriodCountConsolidatorBase[T, TradeBar](pmaxCount, ptimespan) {

  def  workingBar: TradeBar = workingData.asInstanceOf[TradeBar]
}
