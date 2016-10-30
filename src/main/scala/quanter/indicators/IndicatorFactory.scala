package quanter.indicators

import quanter.data.market.TradeBar
import quanter.indicators.window.{ExponentialMovingAverage, SimpleMovingAverage}


/**
  *
  */
class IndicatorFactory {

  def createDataPointIndicator(name: String, param: String): IndicatorBase[IndicatorDataPoint] = {
//    var indicator: IndicatorBase[IndicatorDataPoint] = null
val params = param.split(IndicatorFactory.splitChar)
    name match {
      case IndicatorFactory.MACD => _macd(params(0).toInt, params(1).toInt, params(2).toInt)
      case IndicatorFactory.SMA => _sma(params(0).toInt)
      case IndicatorFactory.EMA => _ema(params(0).toInt)
      case IndicatorFactory.KAMA => _kama(params(0).toInt)
      case IndicatorFactory.RSI => _rsi(params(0).toInt)
      case _ => null
    }
  }


  def createTradeBarIndicator(name: String, param: String) : TradeBarIndicator = {
    val params = param.split(IndicatorFactory.splitChar)
    name match {
      case IndicatorFactory.PSAR => _psar(params(0).toDouble, params(1).toDouble, params(2).toDouble)
      case IndicatorFactory.MFI => _mfi(params(0).toInt)
      case IndicatorFactory.OBV => _obv()
      case _ => null
    }
  }

  private def _macd(fastPeriod: Int, slowPeriod: Int, signalPeriod: Int) = {
    new MovingAverageConvergenceDivergence(fastPeriod, slowPeriod, signalPeriod, MovingAverageType.Simple)
  }

  private def _sma(period: Int) = {
    new SimpleMovingAverage(period)
  }

  private def _ema(period: Int) = {
    new ExponentialMovingAverage(period)
  }

  private def _kama(period: Int) = {
    new KaufmanAdaptiveMovingAverage(period)
  }

  private def _rsi(period: Int) = {
    new RelativeStrengthIndex(period)
  }

  private def _psar(afStart: Double, afIncrement: Double, afMax: Double) = {
    new ParabolicStopAndReverse(afStart, afIncrement, afMax)
  }

  private def _mfi(period: Int) = {
    new MoneyFlowIndex(period)
  }

  private def _obv() = {
    new OnBalanceVolume("OBV")
  }
}

object IndicatorFactory {
  val splitChar = "~"

  // DATAPOINT INDICATOR
  val MACD = "MACD"
  val EMA = "EMA"
  val SMA = "SMA"
  val RSI = "RSI"
  val SAR = "SAR"
  val KDJ = "KDJ"
  val DMI = "DMI"
  val KAMA = "KAMA"

  // TRADEBAR INDICATOR
  val PSAR = "PSAR"
  val MFI = "MFI"
  val OBV = "OBV"

}
