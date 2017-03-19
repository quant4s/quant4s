package org.quant4s

object SecurityType extends Enumeration {
  type SecurityType = Value
  val Base, Equity, Option, Commodity, Forex, Future, Cfd = Value
}


object AccountType extends Enumeration {
  type AccountType = Value
  val Margin, Cash = Value
}

object MarketDataType extends Enumeration {
  type MarketDataType = Value
  val Base, TradeBar, Tick, Auxiliary, QuoteBar = Value
}

object DataFeedEndpoint extends Enumeration {
  type DataFeedEndpoint = Value
  val Backtesting, FileSystem, LiveTrading, Database = Value
}

object OptionStyle extends Enumeration {
  type OptionStyle = Value
  val American, European = Value
}

object OptionRight extends Enumeration {
  type OptionRight = Value
  val Call, Put = Value
}

object Resolution extends Enumeration {
  type Resolution = Value
  val Tick, Second, Minute, Minute5, Minute15, Minute30, Hour, Daily = Value
}

object AlgorithmStatus extends Enumeration {
  type AlgorithmStatus = Value
  val DeployError, InQueue, Running, Stopped,
  Liquidated,Deleted, Completed, RuntimeError,
  Invalid, LoggingIn, Initializing, History = Value

}

object TickType extends Enumeration
{
  type TickType = Value
  val Trade, Quote = Value
}