package quanter.data.market

import java.util.Date

import quanter.MarketDataType
import quanter.TickType.TickType
import quanter.data.BaseData

import scala.collection.mutable.ArrayBuffer

/**
  *
  */
class Tick(val symbol: Symbol, val tickType: TickType, val bidPrice: Double, val askPrice: Double, val bidSize: Long,
           val askSize: Long, val quantity: Int) extends BaseData {
//  val _tickType = ptickType
//  val bidPrice: BigDecimal = pbidPrice
//  val askPrice: BigDecimal = paskPrice
//  val bidSize: Long = pbidSize
//  val askSize: Long = paskSize

  def lastPrice: BigDecimal = value

  dataType = MarketDataType.Tick
  value = (bidPrice + askPrice) / 2

}

class Ticks(frontier: Date) extends DataDictionary[ArrayBuffer[Tick]](frontier)  {

}
