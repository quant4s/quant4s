package org.quant4s.data.market

import org.quant4s.data.BaseData

/**
  *
  */
class SnapData extends TradeBar{
  override def toJson = "{\"symbol\":\"%s\",\"open\":%f,\"high\":%f,\"low\":%f,\"close\":%f,\"time\":%d}".format(symbol, open, high, low, close, time.getTime())
}
