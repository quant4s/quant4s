package com.msilb.scalanda.restapi.model

import spray.json._

sealed trait OrderType

object OrderType {

  case object Limit extends OrderType {
    override def toString = "limit"
  }

  case object Stop extends OrderType {
    override def toString = "stop"
  }

  case object MarketIfTouched extends OrderType {
    override def toString = "marketIfTouched"
  }

  case object Market extends OrderType {
    override def toString = "market"
  }

  implicit object OrderTypeJsonFormat extends JsonFormat[OrderType] {
    def write(x: OrderType): JsValue = JsString(x.toString)

    def read(value: JsValue): OrderType = value match {
      case JsString(x) => x match {
        case "limit" => Limit
        case "stop" => Stop
        case "marketIfTouched" => MarketIfTouched
        case "market" => Market
      }
      case x => deserializationError("Expected OrderType as JsString, but got " + x)
    }
  }

}
