package org.quant4s.indicators.composite

import java.util.Date

import org.quant4s.data.BaseData
import org.quant4s.indicators.{IndicatorBase, IndicatorDataPoint}

/**
  *
  */

class CompositeIndicator[T <: BaseData](pname: String, pleft: IndicatorBase[T], pright: IndicatorBase[T], pcomposer: (IndicatorBase[T], IndicatorBase[T]) => Double) extends IndicatorBase[T](pname) {
  val left = pleft
  val right = pright
  val composer =  pcomposer
  _configureEventHandlers()

  def this(pleft: IndicatorBase[T], pright: IndicatorBase[T], pcomposer: (IndicatorBase[T], IndicatorBase[T]) => Double) {
    this("COMPOSE(%s, %s)".format(pleft.name, pright.name), pleft, pright, pcomposer)
  }

  private def _configureEventHandlers() = {

    val leftIsConstant = true  // Left.getClass().isAssignableFrom(_)
    val rightIsConstant = true // Right.GetType().IsSubclassOfGeneric(typeof (ConstantIndicator<>));

    var newLeftData: IndicatorDataPoint = null
    var newRightData: IndicatorDataPoint = null
    left.updatedHandlers.+=(updated =>  {
      newLeftData = updated
      if(newRightData != null || rightIsConstant) {
        val t: T = null.asInstanceOf[T]
        t.time = _maxTime(updated)
        update(t)

        newLeftData = null
        newRightData = null
      }

    })

    right.updatedHandlers.+=(updated =>  {
      newRightData = updated
      if(newLeftData != null || leftIsConstant) {
        val t: T = null.asInstanceOf[T]
        t.time = _maxTime(updated)
        update(t)

        newLeftData = null
        newRightData = null
      }

    })

  }

  private  def _maxTime(updated: IndicatorDataPoint): Date = {
    new Date(math.max(updated.time.getTime, math.max(right.current.time.getTime, left.current.time.getTime)))
  }

  override def isReady: Boolean = left.isReady && right.isReady
  override def reset: Unit = {
    left.reset
    right.reset
    super.reset
  }

  override def computeNextValue(input: T): Double = composer.apply(left, right)
}
