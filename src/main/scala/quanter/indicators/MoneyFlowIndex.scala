/**
  *
  */
package quanter.indicators
import quanter.data.market.TradeBar
import quanter.indicators.window.Sum
import  quanter.indicators.IndicatorExtensions._

/**
  *
  */
class MoneyFlowIndex(pname: String, pperiod: Int) extends TradeBarIndicator(pname){
  def this(period: Int) {
    this("MFI" + period, period)
  }

  val positiveMoneyFlow: IndicatorBase[IndicatorDataPoint] = new Sum(pname + "_PositiveMoneyFlow", pperiod)
  val negativeMoneyFlow: IndicatorBase[IndicatorDataPoint] = new Sum(pname + "_NegativeMoneyFlow", pperiod)

  private var _previousTypicalPrice: Double = 0.0
  def previousTypicalPrice = _previousTypicalPrice


  override def isReady: Boolean = positiveMoneyFlow.isReady && negativeMoneyFlow.isReady

  override def reset: Unit = {
    _previousTypicalPrice = 0.0
    negativeMoneyFlow.reset
    positiveMoneyFlow.reset
    super.reset
  }

  override def computeNextValue(input: TradeBar): Double = {
    val typicalPrice = (input.high + input.low + input.close)/3.0
    val moneyFlow = typicalPrice*input.volume

    positiveMoneyFlow.update(input.time, if(typicalPrice > previousTypicalPrice) moneyFlow else 0 )
    negativeMoneyFlow.update(input.time, if(typicalPrice > previousTypicalPrice) 0 else moneyFlow)
    _previousTypicalPrice = typicalPrice

    val totalMoneyFlow = positiveMoneyFlow.current.value + negativeMoneyFlow.current.value

    if(totalMoneyFlow == 0) 100.0
    else 100 * positiveMoneyFlow.current.value / totalMoneyFlow
  }
}
