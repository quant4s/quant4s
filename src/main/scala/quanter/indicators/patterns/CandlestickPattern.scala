/**
  *
  */
package quanter.indicators.patterns

import quanter.data.market.TradeBar
import quanter.indicators.patterns.CandleSettingType.CandleSettingType
import quanter.indicators.window.WindowIndicator

/**
  *
  */
abstract class CandlestickPattern(pname: String, pperiod: Int) extends WindowIndicator[TradeBar](pname, pperiod){
  def getRealBody( tradeBar: TradeBar) : Double = math.abs(tradeBar.close - tradeBar.open)

  def getLowerShadow(tradeBar: TradeBar): Double = (if(tradeBar.close >= tradeBar.open) tradeBar.open else tradeBar.close) - tradeBar.low

  def getUpperShadow(tradeBar: TradeBar): Double = tradeBar.high - (if(tradeBar.close >= tradeBar.open) tradeBar.close else tradeBar.open)

  def isGapUp(tradeBar: TradeBar, previousBar: TradeBar) = tradeBar.low > previousBar.high

  def isGapDown(tradeBar: TradeBar, previousBar: TradeBar) = tradeBar.high < previousBar.low

  def isRealBodyGapUp(tradeBar: TradeBar, previousBar: TradeBar) = math.min(tradeBar.open, tradeBar.close) > math.max(previousBar.open, previousBar.close)

  def isRealBodyGapDown(tradeBar: TradeBar, previousBar: TradeBar) = math.max(tradeBar.open, tradeBar.close) < math.min(previousBar.open, previousBar.close)

  def isContained(tradeBar: TradeBar, previousBar: TradeBar) = {
    if(previousBar.high >= tradeBar.high && previousBar.low <= tradeBar.low) true
    else false
  }

  def getHighLowRange(tradeBar: TradeBar) = tradeBar.high - tradeBar.low

  def getCandleRange(settingType: CandleSettingType, tradeBar: TradeBar): Double = {
    CandleSetting.get(settingType).rangeType match {
      case CandleRangeType.HighLow => getHighLowRange(tradeBar)
      case CandleRangeType.RealBody => getRealBody(tradeBar)
      case CandleRangeType.Shadows => getLowerShadow(tradeBar) + getUpperShadow(tradeBar)
      case _ => 0
    }
  }

}
