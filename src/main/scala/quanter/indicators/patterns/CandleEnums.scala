/**
  *
  */
package quanter.indicators.patterns

/**
  *
  */
object CandleSettingType extends Enumeration {
  type CandleSettingType = Value
  val BodyLong,
    BodyVeryLong,
    BodyShort,
    BodyDoji,
    ShadowLong,
    ShadowVeryLong,
    ShadowShort,
    ShadowVeryShort,
    Near,
    Far,
    Equal = Value

}

object CandleRangeType extends  Enumeration {
  type CandleRangeType = Value
  val RealBody,
    HighLow,
    Shadows = Value
}

object CandleColor extends Enumeration {
  type CandleColr = Value
  val Red,
    Green = Value
}