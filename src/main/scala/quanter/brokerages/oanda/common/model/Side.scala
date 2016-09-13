/**
  *
  */
package quanter.brokerages.oanda.common.model

import spray.json.{JsString, JsValue, JsonFormat, deserializationError}

sealed trait Side

object Side {

  case object Buy extends Side {
    override def toString = "buy"
  }

  case object Sell extends Side {
    override def toString = "sell"
  }

  implicit object SideJsonFormat extends JsonFormat[Side] {
    def write(x: Side): JsValue = JsString(x.toString)

    def read(value: JsValue): Side = value match {
      case JsString(x) => x match {
        case "buy" => Buy
        case "sell" => Sell
      }
      case x => deserializationError("Expected Side as JsString, but got " + x)
    }
  }

}
