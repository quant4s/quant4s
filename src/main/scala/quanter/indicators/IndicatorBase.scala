package quanter.indicators

import java.util.Date

import org.slf4s._
import quanter.Asserts
import quanter.data.BaseData

import scala.collection.mutable.ListBuffer

/**
  *
  */
abstract class IndicatorBase[T <: BaseData] (pname: String) extends Comparable[IndicatorBase[T]] with Logging{
  type IndicatorUpdatedHandler = IndicatorDataPoint => Unit

  val updatedHandlers = ListBuffer.empty[IndicatorUpdatedHandler]

  private var _previousInput = null.asInstanceOf[T]
  def name = pname
  def isReady: Boolean
  private var _current: IndicatorDataPoint = new IndicatorDataPoint(new Date(0), 0.0)
  def current= _current
  protected def current_= (newValue: IndicatorDataPoint){ _current = newValue }

  private var _pre: IndicatorDataPoint = new IndicatorDataPoint(new Date(0), 0.0)
  def pre = _pre
  protected def pre_=(newValue: IndicatorDataPoint){ _pre = newValue}

  private var _samples: Long = 0
  def samples = _samples

  def symbol = _current.symbol

  var time: Date =  new Date(0)
  override def compareTo(o: IndicatorBase[T]) = current.compareTo(o.current)

  def update(input: T) =  {
    log.debug("update indicator value to %s".format(input.value))
    time = input.time
    Asserts.assert(!(_previousInput != null.asInstanceOf[T] && input.time.before(_previousInput.time)))
    if(_previousInput != input) {
      _samples += 1
      _previousInput = input
      val nextResult = validateAndComputeNextValue(input)
      if(nextResult.status == IndicatorStatus.Success) {
        _pre = _current
        _current = new IndicatorDataPoint(input.symbol,input.time, nextResult.value)
        onUpdated(_current)
      }
    }

    isReady
  }

  def reset:Unit = {
    _samples = 0
    _previousInput = null.asInstanceOf[T]
    _current = new IndicatorDataPoint(new Date(0), 0.0)
  }

  def validateAndComputeNextValue(input: T) = {
    new IndicatorResult(computeNextValue(input))
  }

  def computeNextValue(input: T): Double

  private def onUpdated(data: IndicatorDataPoint): Unit = {
    updatedHandlers.foreach(_.apply(data))
  }

  def goldenCross = if(_pre.value < 0 && _current.value >= 0) true else false
  def deathCross = if(_pre.value > 0 && _current.value <= 0) true else false

  override def toString() = {
    _current.value.toString()
  }

  override def equals(obj: scala.Any): Boolean =  {
    var ret: Boolean = false
    if(obj == null) ret = false
    //    if (obj.GetType().IsSubclassOf(typeof (IndicatorBase<>))) return ReferenceEquals(this, obj);

    val converted = obj.toString().toDouble
    (_current.value == converted)
  }

  def toDetailedString() = {
    "%s - %s".format(name, this.toString())
  }

  def toJson = "{\"symbol\":%s,\"value\":%f%f}".format(symbol,  current.value)

  // ================== override operator ===============================
  def +(right: IndicatorBase[IndicatorDataPoint]) = {
    this.current.value + right.current.value
  }

  def +(right: Double) = {
    this.current.value + right
  }

  def -(right: IndicatorBase[IndicatorDataPoint]) = {
    this.current.value - right.current.value
  }

  def -(right: Double) = {
    this.current.value - right
  }

  def *(right: IndicatorBase[IndicatorDataPoint]) = {
    this.current.value * right.current.value
  }

  def *(right: Double) = {
    this.current.value * right
  }

  def /(right: IndicatorBase[IndicatorDataPoint]) = {
    this.current.value / right.current.value
  }

  def /(right: Double) = {
    this.current.value / right
  }
}

