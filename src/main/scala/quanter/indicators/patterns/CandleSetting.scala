/**
  *
  */
package quanter.indicators.patterns

import quanter.indicators.patterns.CandleRangeType.CandleRangeType
import quanter.indicators.patterns.CandleSettingType.CandleSettingType

import scala.collection.immutable.Map

/**
  *
  */
case class CandleSetting(rangeType: CandleRangeType, averagePeriod: Int, factor: Double)

object CandleSetting {
  val defaultSettings = Map[CandleSettingType, CandleSetting]( CandleSettingType.BodyLong -> new CandleSetting(CandleRangeType.RealBody, 10, 1),
    CandleSettingType.BodyVeryLong -> CandleSetting(CandleRangeType.RealBody, 10, 3),
    CandleSettingType.BodyShort -> CandleSetting(CandleRangeType.RealBody, 10, 1),
    CandleSettingType.BodyDoji -> CandleSetting(CandleRangeType.HighLow, 10, 0.1),
    CandleSettingType.ShadowLong -> CandleSetting(CandleRangeType.RealBody, 0, 1),
    CandleSettingType.ShadowVeryLong -> CandleSetting(CandleRangeType.RealBody, 0, 2),
    CandleSettingType.ShadowShort -> CandleSetting(CandleRangeType.Shadows, 10, 1),
    CandleSettingType.ShadowVeryShort -> CandleSetting(CandleRangeType.HighLow, 10, 0.1),
    CandleSettingType.Near -> CandleSetting(CandleRangeType.HighLow, 5, 0.2),
    CandleSettingType.Far -> CandleSetting(CandleRangeType.HighLow, 5, 0.6),
    CandleSettingType.Equal -> CandleSetting(CandleRangeType.HighLow, 5, 0.05)
  )

  def  get(settingType: CandleSettingType): CandleSetting = defaultSettings.get(settingType).get

}
