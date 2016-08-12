package quanter.data.market

import quanter.data.BaseData

/**
  *
  */
class SnapData extends TradeBar{

  override def toJson = """{"symbol":"%s","open":"%d","high":"%d","low":"%d","close":"%d","time":"%s"}""".format(symbol, 1,1,1,1,"")
//  override def toJson = """{"symbol":"%s","open":"%d","high":"%d","low":"%d","close":"%d","time":"%s"}""".format(symbol, open, high, low, close,"")
}
