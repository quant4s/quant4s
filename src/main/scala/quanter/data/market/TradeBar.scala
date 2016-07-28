package quanter.data.market

import java.util.Date

import quanter.{MarketDataType, TimeSpan}
import quanter.data.BaseData

/**
  *
  */
class TradeBar extends BaseData with TBar{
  var volume: Long = 0
  var turnover: Double = 0
  var period: TimeSpan = null
  _init()

  // init
  private def _init() = {
    dataType = MarketDataType.TradeBar
    symbol = ""
  }
}

class TradeBars(frontier: Date) extends DataDictionary[TradeBar](frontier) {

}
