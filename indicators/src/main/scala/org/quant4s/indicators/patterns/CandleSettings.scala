/**
  *
  */
package org.quant4s.indicators.patterns

import org.quant4s.indicators.patterns.CandleRangeType.CandleRangeType
import org.quant4s.indicators.patterns.CandleSettingType.CandleSettingType

import scala.collection.immutable.Map

/**
  *
  */
case class CandleSettings(rangeType: CandleRangeType, averagePeriod: Int, factor: Double)

object CandleSettings {
  val defaultSettings = Map[CandleSettingType, CandleSettings]( CandleSettingType.BodyLong -> new CandleSettings(CandleRangeType.RealBody, 10, 1),
    CandleSettingType.BodyVeryLong -> CandleSettings(CandleRangeType.RealBody, 10, 3),
    CandleSettingType.BodyShort -> CandleSettings(CandleRangeType.RealBody, 10, 1),
    CandleSettingType.BodyDoji -> CandleSettings(CandleRangeType.HighLow, 10, 0.1),
    CandleSettingType.ShadowLong -> CandleSettings(CandleRangeType.RealBody, 0, 1),
    CandleSettingType.ShadowVeryLong -> CandleSettings(CandleRangeType.RealBody, 0, 2),
    CandleSettingType.ShadowShort -> CandleSettings(CandleRangeType.Shadows, 10, 1),
    CandleSettingType.ShadowVeryShort -> CandleSettings(CandleRangeType.HighLow, 10, 0.1),
    CandleSettingType.Near -> CandleSettings(CandleRangeType.HighLow, 5, 0.2),
    CandleSettingType.Far -> CandleSettings(CandleRangeType.HighLow, 5, 0.6),
    CandleSettingType.Equal -> CandleSettings(CandleRangeType.HighLow, 5, 0.05)
  )

  def  get(settingType: CandleSettingType): CandleSettings = defaultSettings.get(settingType).get

}
