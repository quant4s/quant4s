/**
  *
  */
package quanter.consolidators

import quanter.{MarketDataType, TimeSpan}
import quanter.data.market.{Tick, TradeBar}

/**
  *
  */
class TickConsolidator(pmaxCount: Option[Int] = None, ptimespan: Option[TimeSpan] = None) extends TradeBarConsolidatorBase[Tick](pmaxCount, ptimespan) {
  override protected def aggregateBar(workingBar: TradeBar, data: Tick): TradeBar = {
    var bar: TradeBar = null
    if(workingBar == null) {
      bar = new TradeBar()
      {
        time = getRoundedBarTime(data.time)
        symbol = data.symbol
        open = data.value
        high = data.value
        low = data.value
        close = data.value
        volume = data.quantity
        dataType = data.dataType
      }
    } else {
      workingBar.close = data.value
      workingBar.volume += data.quantity
      if (data.value < workingBar.low) workingBar.low = data.value
      if (data.value > workingBar.high) workingBar.high = data.value

      bar = workingBar
    }
    bar
  }
}
