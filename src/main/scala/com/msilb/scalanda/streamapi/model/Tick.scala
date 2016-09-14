package com.msilb.scalanda.streamapi.model

import java.time.ZonedDateTime

import com.msilb.scalanda.common.util.DateUtils.DateJsonFormat
import spray.json.DefaultJsonProtocol

case class Tick(instrument: String, time: ZonedDateTime, bid: Double, ask: Double)

object TickJsonProtocol extends DefaultJsonProtocol {
  implicit val tickFormat = jsonFormat4(Tick)
}
