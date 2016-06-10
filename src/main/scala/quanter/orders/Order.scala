package quanter.orders

import java.util.Date

import quanter.orders.OrderDirection._
import quanter.orders.OrderType.OrderType
import quanter.{Asserts, SecurityType}

/**
  *
  */
object Order {
  def createOrder(request : String) = {
    val orderType = "limit"
    val symbol = ""
    val quantity = 100
    val limitPrice = 12.3
    val time = new Date()
    orderType match {
      case "limit" => new LimitOrder(symbol, quantity, limitPrice, time)
      case "market" => null
    }
  }
}

/**
  * 订单抽象类
  * @param symbol
  * @param quantity
  * @param time
  * @param tag
  */
abstract class Order(val symbol: String, var quantity: Int, time: Date, var tag: String) {
  var id = 0
//  var symbol : String = psymbol
  var price : Double = 0
//  var quantity = pquantity
//  var time : Date = ptime
//  var tag = ptag
  var status = OrderStatus.None
  def securityType = SecurityType.Base
  def direction = {if (quantity > 0) Buy else if(quantity < 0) Sell else Hold}
  def orderType : OrderType
  def absoluteQuantity = math.abs(quantity)
  def value = quantity * price
  def tradeId = ""

  def this() {
    this(null, 0, null, "")
  }

  //var request : OrderRequest = null

  def applyUpdateOrderRequest(request : String) : Unit = {
    val torderId = 0
    var tquantity = None
    var ttag: String = null
    Asserts.assert(torderId == id)

    if(tquantity != None) quantity = tquantity.get
    if(ttag != null) tag = ttag
  }
}

//class MarketOrder(symbol: String, quantity: Int, time:Date, tag: String="") extends Order(symbol, quantity, time, tag){
//  def this() = {
//    this(null, 0, null)
//  }
//  override def orderType = OrderType.Market
//}
//
//class StopMarketOrder(psymbol: String, pquantity: Int, pstopPrice : Double, ptime:Date, ptag: String="") extends Order(psymbol, pquantity, ptime, ptag){
//  val _stopPrice = pstopPrice
//  def stopPrice_ = _stopPrice
//  if(tag == "") this.tag = "Limit Price: " + _stopPrice
//  override def orderType = OrderType.StopMarket
//}

/**
  * 限价订单
  * @param symbol
  * @param quantity
  * @param limitPrice
  * @param time
  */
class LimitOrder(symbol: String, quantity: Int, var limitPrice:Double, time:Date) extends Order(symbol, quantity, time, "") {
//  var _limitPrice : Double = plimitPrice
//  def limitPrice = _limitPrice
  if(tag == "") tag = "Limit Price: " + limitPrice
  override def orderType: OrderType = OrderType.Limit
  override def applyUpdateOrderRequest(request : String) : Unit = {
    super.applyUpdateOrderRequest(request)
    val lp = None // request.limitPrice
    if(lp != None) limitPrice = lp.get
  }

  def this() {
    this(null, 0, 0, null)
  }
}

//class StopLimitOrder(psymbol: String, pquantity: Int, pstopPrice: Double, plimitPrice:Double, ptime:Date, ptag: String="") extends Order(psymbol, pquantity, ptime, ptag)  {
//  var stopPrice = pstopPrice
//  var limitPrice = plimitPrice
//  var stopTriggered = false;
//
//  override def orderType: OrderType = OrderType.StopLimit
//  override def applyUpdateOrderRequest(request : UpdateOrderRequest) : Unit = {
//    super.applyUpdateOrderRequest(request)
//    if(request.stopPrice != None) stopPrice = request.stopPrice.get
//    if(request.limitPrice != None) limitPrice = request.limitPrice.get
//  }
//}

//class MarketOnCloseOrder(psymbol: String, pquantity: Int, ptime:Date, ptag: String="")  extends Order(psymbol, pquantity, ptime, ptag) {
//  override def orderType: OrderType = OrderType.MarketOnClose
//  def this() {
//    this(null, 0, null)
//  }
//}
//
//class MarketOnOpenOrder(psymbol: String, pquantity: Int, ptime:Date, ptag: String="")  extends Order(psymbol, pquantity, ptime, ptag) {
//  override def orderType: OrderType = OrderType.MarketOnOpen
//  def this() {
//    this(null, 0, null)
//  }
//}
