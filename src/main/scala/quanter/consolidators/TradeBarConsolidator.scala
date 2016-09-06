package quanter.consolidators

import quanter.{MarketDataType, TimeSpan}
import quanter.Resolution.Resolution
import quanter.data.market.TradeBar
import quanter.CommonExtensions.ResolutionExt

/**
  *
  */
class TradeBarConsolidator(pmaxCount: Option[Int] = None, ptimespan: Option[TimeSpan] = None) extends TradeBarConsolidatorBase[TradeBar](pmaxCount, ptimespan){
  override protected def aggregateBar(workingBar: TradeBar, data: TradeBar): TradeBar = {
    var bar: TradeBar = null
    if(workingBar == null) {
      bar = new TradeBar()
      {
        time = getRoundedBarTime(data.time)
        symbol = data.symbol
        open = data.open
        high = data.high
        low = data.low
        close = data.close
        volume = data.volume
        dataType = MarketDataType.TradeBar
        period = data.period
      }
    } else {
      workingBar.close = data.close
      workingBar.volume += data.volume
      workingBar.period += data.period // TODO: data.period is null
      if (data.low < workingBar.low) workingBar.low = data.low
      if (data.high > workingBar.high) workingBar.high = data.high

      bar = workingBar
    }
    bar
  }
}

object TradeBarConsolidator {
  def fromResolution(resolution: Resolution) = new TradeBarConsolidator(ptimespan = Some(resolution.toTimeSpan()))
}
