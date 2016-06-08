package quanter.indicators

import quanter.data.BaseData

/**
  *
  */
class FunctionalIndicator[T <: BaseData](pname: String, pcomputeNextValue: (T) => Double, pisReady: (IndicatorBase[T]) => Boolean, preset: ()=> Unit) extends IndicatorBase[T](pname)  {
  type Action = ( ()=> Unit)
  private val _reset: Action = preset

  private def _isReady = pisReady
  override def isReady = _isReady.apply(this)

  def this(pname: String, pcomputeNextValue: (T) => Double, pisReady: (IndicatorBase[T]) => Boolean) {
    this(pname, pcomputeNextValue, pisReady, null)
  }

  override def computeNextValue(input: T) : Double = {
    pcomputeNextValue.apply(input)
  }
}
