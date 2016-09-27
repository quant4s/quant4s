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
  var period: TimeSpan = TimeSpan.fromMinutes(1)

  private var _open: Double = 0
  private var _high: Double = 0
  private var _low: Double = 0

  private var _initialized = false

  _init()

  // init
  private def _init() = {
    dataType = MarketDataType.TradeBar
    symbol = ""
  }

  override def open: Double = _open
  def open_=(newValue: Double) {
    initialize(newValue)
    _open = newValue
  }

  override def high: Double = _high
  def high_=(newValue: Double)  {
    initialize(newValue)
    _high = newValue
  }

  override def low: Double = _low
  def low_=(newValue: Double)  {
    initialize(newValue)
    _low = newValue
  }
  override def close: Double = this.value
  def close_=(newValue: Double)  {
    initialize(newValue)
    value = newValue
  }

  private def initialize(value: Double): Unit = {
    if(!_initialized) {
      _open = value
      _low = value
      _high = value
      _initialized = true
    }
  }

  override def toJson = "{\"symbol\":\"%s\",\"open\":%f,\"high\":%f,\"low\":%f,\"close\":%f,\"time\":%d}".format(symbol, open, high, low, close, time.getTime())
}

class TradeBars(frontier: Date) extends DataDictionary[TradeBar](frontier) {

}
