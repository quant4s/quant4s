package org.quant4s.consolidators

import org.quant4s.Resolution.Resolution
import org.quant4s.{MarketDataType, TimeSpan}
import org.quant4s.data.market.TradeBar
import org.quant4s.CommonExtensions._

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
    } else if(data.time.after(workingBar.time)){ // 防止意外情况下，发送过来的数据错误
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
