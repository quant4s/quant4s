/**
  *
  */
package org.quant4s.statistics

import org.quant4s.securities.Security
import org.quant4s.statistics.FillGroupingMethod.FillGroupingMethod
import org.quant4s.statistics.FillMatchingMethod.FillMatchingMethod

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  *
  */
class TradeBuilder(groupingMethod: FillGroupingMethod, matchingMethod: FillMatchingMethod) {

  val liveModeMaxTradeCount = 10000
  val liveModeMaxTradeAgeMonths = 12
  val maxOrderIdCacheSize = 1000

//  private readonly Dictionary<Symbol, Position> _positions = new Dictionary<Symbol, Position>();
  private var _positions = mutable.Map[Security, Position]()

  private var _closedTrades = ArrayBuffer[Trade]()
  def closedTrades = _closedTrades

  private var _liveMode = true
  def liveMode_=(newValue: Boolean){_liveMode = newValue}

  //  private readonly Dictionary<Symbol, Position> _positions = new Dictionary<Symbol, Position>();
  //  private readonly FixedSizeHashQueue<int> _ordersWithFeesAssigned = new FixedSizeHashQueue<int>(MaxOrderIdCacheSize);


  def hasOpenPosition(symbol: Security): Boolean = {
    false
  }

//  def processFill(fill: OrderEvent): Unit = {
//    groupingMethod match {
//      case FillGroupingMethod.FillToFill =>
//      case FillGroupingMethod.FlatToFlat =>
//      case FillGroupingMethod.FlatToReduced =>
//    }
//  }

  def ProcessFillUsingFillToFill(): Unit = {
    var position = null.asInstanceOf[Position]
    if(true) {  // no pending trader for symbol
      val p =  new Position()
      p.maxPrice = 0
      p.minPrice = 0
      p.pendingTrades = null
      _positions(new Security("")) = p
    } else {  //
      if(true) {    //相同方向的交易

      } else {  // 相反方向的交易
        //val trade = new Trade()
        if(true) {

        }else {

        }
//        addNewTrade(trade)
      }
    }
  }

  private def addNewTrade(trade: Trade): Unit = {
    _closedTrades += trade
    // 由于内存限制，限定了实盘模式处理的交易记录
    if(_liveMode) {

    }
  }
}

object TradeBuilder {

}

class Position
{
  var pendingTrades = List[Trade]()

  var totalFees = 0.0
  var maxPrice = 0.0
  var minPrice = 0.0
//  internal List<Trade> PendingTrades { get; set; }
//  internal List<OrderEvent> PendingFills { get; set; }


//  public Position()
//  {
//    PendingTrades = new List<Trade>();
//    PendingFills = new List<OrderEvent>();
//  }
}
