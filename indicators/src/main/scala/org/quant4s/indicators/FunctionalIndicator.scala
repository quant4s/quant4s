package org.quant4s.indicators

import org.quant4s.data.BaseData

/**
  *
  */
class FunctionalIndicator[T <: BaseData](pname: String, pcomputeNextValue: (T) => Double, pisReady: (IndicatorBase[T]) => Boolean, preset: ()=> Unit) extends IndicatorBase[T](pname)  {

  def this(pname: String, pcomputeNextValue: (T) => Double, pisReady: (IndicatorBase[T]) => Boolean) {
    this(pname, pcomputeNextValue, pisReady, null)
  }

  private val _reset = preset
  private def _isReady = pisReady
  private val _computeNextValue = pcomputeNextValue

  override def isReady = _isReady.apply(this)

  override def reset = {
    if(_reset != null) _reset.apply()
    super.reset
  }
  override def computeNextValue(input: T) : Double = {
    _computeNextValue.apply(input)
  }

}
