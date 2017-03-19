package quanter.indicators

import org.quant4s.data.market.TradeBar
import quanter.indicators.IndicatorExtensions.IndicatorBaseExt
import quanter.indicators.window.{Maximum, Minimum}

object AroonOscillator {

}

class AroonOscillator(name:String, upPeriod: Int, downPeriod: Int) extends TradeBarIndicator(name){
  val max = new Maximum(name + "_Max", upPeriod + 1);
  val aroonUp = new FunctionalIndicator[IndicatorDataPoint](name + "_AroonUp",
    input => _computeAroonUp(upPeriod, max, input),
    d => max.isReady, ()=>max.reset)
  val min = new Minimum(name + "_Min", downPeriod + 1);
  val aroonDown = new FunctionalIndicator[IndicatorDataPoint](name + "_AroonDown",
    input => _computeAroonDown(downPeriod, min, input),
    d => min.isReady, ()=>min.reset)


  def this(upPeriod: Int, downPeriod: Int) {
    this("%d, %d".format(upPeriod, downPeriod), upPeriod, downPeriod)
  }

  private def _computeAroonUp(upPeriod: Int, max: Maximum, input: IndicatorDataPoint) : Double =
  {
    max.update(input)
    100 * (upPeriod - max.periodsSinceMaximum) / upPeriod
  }

  private def _computeAroonDown(downPeriod: Int, min: Minimum, input: IndicatorDataPoint) : Double =
  {
    min.update(input)
    100 * (downPeriod - min.periodsSinceMinimum) / downPeriod
  }


  override def isReady = aroonUp.isReady && aroonDown.isReady
  override def reset = {
    aroonUp.reset
    aroonDown.reset
    super.reset
  }

  override def computeNextValue(input: TradeBar) = {
    aroonUp.update(input.time, input.high);
    aroonDown.update(input.time, input.low);

    aroonUp - aroonDown
  }

  override def toJson = "{\"symbol\":%s,\"up\":%f,\"down\":%f,\"value\":%f%f}".format(symbol, aroonUp.current.value, aroonDown.current.value, current.value)
}
