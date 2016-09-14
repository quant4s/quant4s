package com.msilb.scalanda.streamapi.model

import java.time.ZonedDateTime

import com.msilb.scalanda.common.util.DateUtils.DateJsonFormat
import spray.json.DefaultJsonProtocol

case class Heartbeat(time: ZonedDateTime)

object HeartbeatJsonProtocol extends DefaultJsonProtocol {
  implicit val heartbeatFormat = jsonFormat1(Heartbeat)
}
