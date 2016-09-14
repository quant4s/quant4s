package com.msilb.scalanda.restapi.model

import java.time.ZonedDateTime

import com.msilb.scalanda.common.util.DateUtils._
import spray.json.DefaultJsonProtocol

sealed trait Candle {
  def time: ZonedDateTime

  def volume: Int

  def complete: Boolean
}

object Candle {

  case class MidPointBasedCandle(time: ZonedDateTime,
                                 openMid: Double,
                                 highMid: Double,
                                 lowMid: Double,
                                 closeMid: Double,
                                 volume: Int,
                                 complete: Boolean) extends Candle

  case class BidAskBasedCandle(time: ZonedDateTime,
                               openBid: Double,
                               highBid: Double,
                               lowBid: Double,
                               closeBid: Double,
                               openAsk: Double,
                               highAsk: Double,
                               lowAsk: Double,
                               closeAsk: Double,
                               volume: Int,
                               complete: Boolean) extends Candle

  object CandleJsonProtocol extends DefaultJsonProtocol {
    implicit val midPointBasedCandleFormat = jsonFormat7(MidPointBasedCandle)
    implicit val bidAskBasedCandleFormat = jsonFormat11(BidAskBasedCandle)
  }

}
