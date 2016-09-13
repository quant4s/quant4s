/**
  *
  */
package quanter.brokerages.oanda.streamapi.model

import java.time.ZonedDateTime
import quanter.brokerages.oanda.common.util.DateUtils.DateJsonFormat
import spray.json.DefaultJsonProtocol

case class Heartbeat(time: ZonedDateTime)

object HeartbeatJsonProtocol extends DefaultJsonProtocol {
  implicit val heartbeatFormat = jsonFormat1(Heartbeat)
}