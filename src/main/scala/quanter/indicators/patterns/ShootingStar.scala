/**
  *
  */
package quanter.indicators.patterns
import quanter.data.market.TradeBar
import quanter.indicators.window.ReadOnlyWindow

/**
  *
  */
class ShootingStar(pname: String) extends CandlestickPattern(pname,  math.max(math.max(CandleSettings.get(CandleSettingType.BodyShort).averagePeriod, CandleSettings.get(CandleSettingType.ShadowLong).averagePeriod),
  CandleSettings.get(CandleSettingType.ShadowVeryShort).averagePeriod) + 1 + 1) {

  def this() {
    this("SHOOTING STAR")
  }

  val _bodyShortAveragePeriod = CandleSettings.get(CandleSettingType.BodyShort).averagePeriod
  val _shadowLongAveragePeriod = CandleSettings.get(CandleSettingType.ShadowLong).averagePeriod
  val _shadowVeryShortAveragePeriod = CandleSettings.get(CandleSettingType.ShadowVeryShort).averagePeriod
  private var _bodyShortPeriodTotal = 0.0
  private var _shadowLongPeriodTotal = 0.0
  private var _shadowVeryShortPeriodTotal = 0.0

  override def isReady: Boolean = samples >= period

  override def reset: Unit = {
    _bodyShortPeriodTotal = 0.0
    _shadowLongPeriodTotal = 0.0
    _shadowVeryShortPeriodTotal = 0.0
    super.reset
  }

  override protected def computeNextValue(window: ReadOnlyWindow[TradeBar], input: TradeBar): Double = {
    if (!isReady)
    {
      if (samples >= period - _bodyShortAveragePeriod)
      {
        _bodyShortPeriodTotal += getCandleRange(CandleSettingType.BodyShort, input);
      }

      if (samples >= period - _shadowLongAveragePeriod)
      {
        _shadowLongPeriodTotal += getCandleRange(CandleSettingType.ShadowLong, input);
      }

      if (samples >= period - _shadowVeryShortAveragePeriod)
      {
        _shadowVeryShortPeriodTotal += getCandleRange(CandleSettingType.ShadowVeryShort, input);
      }

      0
    } else {
      var value = 0.0
      if (
      // small rb
        getRealBody(input) < getCandleAverage(CandleSettingType.BodyShort, _bodyShortPeriodTotal, input) &&
          // long upper shadow
          getUpperShadow(input) > getCandleAverage(CandleSettingType.ShadowLong, _shadowLongPeriodTotal, input) &&
          // very short lower shadow
          getLowerShadow(input) < getCandleAverage(CandleSettingType.ShadowVeryShort, _shadowVeryShortPeriodTotal, input) &&
          // gap up
          isRealBodyGapUp(input, window.get(1))
      )
      value = -1.0
      else
      value = 0.0

      // add the current range and subtract the first range: this is done after the pattern recognition
      // when avgPeriod is not 0, that means "compare with the previous candles" (it excludes the current candle)

      _bodyShortPeriodTotal += getCandleRange(CandleSettingType.BodyShort, input) -
        getCandleRange(CandleSettingType.BodyShort, window.get(_bodyShortAveragePeriod))

      _shadowLongPeriodTotal += getCandleRange(CandleSettingType.ShadowLong, input) -
        getCandleRange(CandleSettingType.ShadowLong, window.get(_shadowLongAveragePeriod))

      _shadowVeryShortPeriodTotal += getCandleRange(CandleSettingType.ShadowVeryShort, input) -
        getCandleRange(CandleSettingType.ShadowVeryShort, window.get(_shadowVeryShortAveragePeriod))

      value
    }

  }
}
