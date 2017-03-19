/**
  *
  */
package org.quant4s.consolidators

import org.quant4s.TimeSpan
import org.quant4s.MarketDataType
import org.quant4s.data.market.{Tick, TradeBar}

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
    } else if(data.time.after(workingBar.time)){ // 防止意外情况下，发送过来的数据错误
      workingBar.close = data.value
      workingBar.volume += data.quantity
      if (data.value < workingBar.low) workingBar.low = data.value
      if (data.value > workingBar.high) workingBar.high = data.value

      bar = workingBar
    }
    bar
  }
}
