package com.msilb.scalanda.restapi.model

sealed trait CandleFormat

object CandleFormat {

  case object MidPoint extends CandleFormat {
    override def toString = "midpoint"
  }

  case object BidAsk extends CandleFormat {
    override def toString = "bidask"
  }

}
