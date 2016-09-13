/**
  *
  */
package quanter.brokerages.oanda.restapi.model

import quanter.brokerages.oanda.common.util.DateUtils._

sealed trait CandleFormat

object CandleFormat {

  case object MidPoint extends CandleFormat {
    override def toString = "midpoint"
  }

  case object BidAsk extends CandleFormat {
    override def toString = "bidask"
  }

}
