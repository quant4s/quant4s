package quanter.data

import java.util.Date

import quanter.MarketDataType
import quanter.MarketDataType._

/**
  *
  */
trait TBaseData{
  var dataType: MarketDataType = MarketDataType.Base
  var time: Date = new Date()
  var symbol: String = ""
  var value: Double = 0
  def price: Double
}

class BaseData extends  TBaseData{

  private var _isFillForward = false;
  def isFillForward = _isFillForward

  var endTime = time
  def price: Double = value

  final def updateTrade(lastTrade: Double, tradeSize: Long) = {
    update(lastTrade, 0, 0, tradeSize, 0, 0);
  }

  def updateQuote(bidPrice: Double, bidSize: Long, askPrice: Double, askSize: Long) = {
    update(0, bidPrice, askPrice, 0, bidSize, askSize)
  }

  def updateBid(bidPrice: Double, bidSize: Long) = {
    update(0, bidPrice, 0, 0, bidSize, 0)
  }

  def updateAsk(askPrice: Double, askSize: Long) = {
    update(0, 0, askPrice, 0, 0, askSize);
  }

  def update(lastTrade: Double, bidPrice: Double, askPrice: Double, volume: Double, bidSize: Double, askSize:Double) = {
    value = lastTrade
  }

  def toJson = """{"symbol":"000001.XSHE"}"""
}
