package org.quant4s.orders

object OrderStatus extends Enumeration{
  type OrderStatus = Value
  val New, Submitted, PartiallyFilled, Filled, Canceled, None, Invalid = Value
}

object OrderType extends Enumeration{
  type OrderType = Value
  val Market, Limit, MarketOnClose, MarketOnOpen, StopMarket, StopLimit = Value
}

object OrderDirection extends Enumeration {
  type OrderDirection = Value
  val Buy, Sell, Hold = Value
}

object OrderError extends Enumeration {
  type OrderError = Value
  val None, ZeroQuantity, NoData, MarketClosed, InsufficientCapital, MaxOrdersExceeded, TimestampError, GeneralError = Value
}

object OrderField extends Enumeration {
  type OrderField = Value
  val LimitPrice, StopPrice = Value
}

object OrderRequestStatus extends Enumeration {
  type OrderRequestStatus = Value
  val Unprocessed, Processing, Processed, Error = Value
}

object OrderRequestType extends Enumeration {
  type OrderRequestType = Value
  val Submit, Update, Cancel = Value
}

object OrderResponseErrorCode extends Enumeration{
  type OrderResponseErrorCode = Value
  val None, ProcessingError, OrderAlreadyExists, InvalidOrderStatus, UnsupportedRequestType, UnableToFindOrder = Value
}