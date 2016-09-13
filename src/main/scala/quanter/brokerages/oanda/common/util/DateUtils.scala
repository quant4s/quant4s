/**
  *
  */
package quanter.brokerages.oanda.common.util


import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

object DateUtils {

  val dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  implicit object DateJsonFormat extends RootJsonFormat[ZonedDateTime] {

    override def write(obj: ZonedDateTime) = JsString(dateTimeFormatter.format(obj))

    override def read(json: JsValue) = json match {
      case JsString(s) => ZonedDateTime.parse(s, dateTimeFormatter)
      case other => throw new DeserializationException("Cannot parse json value " + other + " as timestamp")
    }
  }

}
