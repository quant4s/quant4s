/**
  *
  */
package quanter.brokerages.oanda.restapi.model

import spray.json._

sealed trait Granularity

object Granularity {

  case object S5 extends Granularity

  case object S10 extends Granularity

  case object S15 extends Granularity

  case object S30 extends Granularity

  case object M1 extends Granularity

  case object M2 extends Granularity

  case object M3 extends Granularity

  case object M4 extends Granularity

  case object M5 extends Granularity

  case object M10 extends Granularity

  case object M15 extends Granularity

  case object M30 extends Granularity

  case object H1 extends Granularity

  case object H2 extends Granularity

  case object H3 extends Granularity

  case object H4 extends Granularity

  case object H6 extends Granularity

  case object H8 extends Granularity

  case object H12 extends Granularity

  case object D extends Granularity

  case object W extends Granularity

  case object M extends Granularity

  implicit object GranularityJsonFormat extends JsonFormat[Granularity] {
    def write(x: Granularity): JsValue = JsString(x.toString)

    def read(value: JsValue): Granularity = value match {
      case JsString(x) => x match {
        case "S5" => S5
        case "S10" => S10
        case "S15" => S15
        case "S30" => S30
        case "M1" => M1
        case "M2" => M2
        case "M3" => M3
        case "M4" => M4
        case "M5" => M5
        case "M10" => M10
        case "M15" => M15
        case "M30" => M30
        case "H1" => H1
        case "H2" => H2
        case "H3" => H3
        case "H4" => H4
        case "H6" => H6
        case "H8" => H8
        case "H12" => H12
        case "D" => D
        case "W" => W
        case "M" => M
      }
      case x => deserializationError("Expected Granularity as JsString, but got " + x)
    }
  }

}