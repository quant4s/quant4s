package org.quant4s.indicators.window

import org.quant4s.data.BaseData
import org.quant4s.indicators.IndicatorBase


/**
  * 描述了作用于滚动窗口的指标
  * @param pname 指标名称
  * @param pperiod 窗口拥有数据的数量
  * @tparam T
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
