package quanter.indicators.window

import quanter.data.BaseData
import quanter.indicators.IndicatorBase

/**
  *
  */
abstract class WindowIndicator[T <: BaseData](pname: String, pperiod: Int) extends IndicatorBase[T](pname){
  private val _window = new RollingWindow[T](pperiod)
  override def isReady = _window.isReady
  def period = _window.size
  override def computeNextValue(input: T): Double =   {
    log.debug("窗口指标计算值: %s".format(_window.toString()))
    _window.add(input);
    computeNextValue(_window, input);
  }
  override def reset = {
    _window.reset
    super.reset
  }

  protected def computeNextValue(window: ReadOnlyWindow[T], input: T): Double

}
