package quanter.indicators.window

import quanter.Asserts
import quanter.indicators.IndicatorDataPoint

/**
  *
  */
class Maximum(name: String, period: Int) extends WindowIndicator[IndicatorDataPoint](name, period){
  var _periodsSinceMaximum = 0.0
  def periodsSinceMaximum = _periodsSinceMaximum

  def this(period: Int) {
    this("MAX" + period, period)
  }
  override def isReady = samples >= period
  override def reset = {
    _periodsSinceMaximum = 0
    super.reset
  }

  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = {
    var ret = 0.0
    if(samples == 1 || input.value >= current.value)  {
      _periodsSinceMaximum = 0
      ret = input.value
    } else if(_periodsSinceMaximum >= period - 1) {
      val maximums = window.toList
      val x = maximums.sortBy(_.value)
      val maximum = x(maximums.size-1)
      _periodsSinceMaximum = window.indexOf(maximum)
      maximum
    } else {
      _periodsSinceMaximum += 1
      ret = current.value
    }

    ret
  }
}

class Minimum (pname: String, pperiod: Int) extends WindowIndicator[IndicatorDataPoint](pname, pperiod){
  private var _periodsSinceMinimum = 0.0
  def periodsSinceMinimum = _periodsSinceMinimum

  def this(period: Int) {
    this("MIN" + period, period)
  }

  override def isReady = samples >= period
  override def reset = {
    _periodsSinceMinimum = 0
    super.reset
  }
  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = {
    if(samples == 1 || input.value <= current.value) {
      _periodsSinceMinimum = 0
      input.value
    } else if(_periodsSinceMinimum >= period - 1){
      val minimums = window.toList
      val x = minimums.sortBy(_.value)
      log.debug("minimus's count is:%s ".format(minimums.size))
      val minimum = x(0)
      _periodsSinceMinimum = window.indexOf(minimum)
      minimum.value
    } else {
      _periodsSinceMinimum += 1
      current.value
    }
  }
}

class Sum(pname: String, pperiod: Int) extends WindowIndicator[IndicatorDataPoint](pname, pperiod){
  var _sum: Double = 0.0

  def this(period: Int) {
    this("SMA", period)
  }

  override def isReady = samples >= period
  override def reset = {
    _sum = 0.0
    super.reset
  }

  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = {
    log.debug("sum is %s before compute next value(window, input: (%s, %s)".format(_sum.toString(), input.time.toString(), input.value))
    Asserts.assert(window != null)
    _sum += input.value
    if(window.isReady) _sum -= window.mostRecentlyRemoved.value
    log.debug("求和" + input.value + "和:" + _sum)
    _sum
  }
}

