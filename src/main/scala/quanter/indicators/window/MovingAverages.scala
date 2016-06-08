package quanter.indicators.window

import quanter.indicators.IndicatorExtensions.{BaseDataExt, IndicatorBaseExt}
import quanter.indicators.{IndicatorBase, IndicatorDataPoint}

/**
* 简单移动均线
* */
class SimpleMovingAverage(pname: String, pperiod: Int) extends WindowIndicator[IndicatorDataPoint](pname, pperiod) {
  val rollingSum: IndicatorBase[IndicatorDataPoint] = new Sum(name + "_Sum", pperiod)

  def this(pperiod: Int) {
    this("SMA", pperiod)
  }
  override def isReady = rollingSum.isReady
  override def reset = {
    rollingSum.reset
    super.reset
  }

  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = {
    rollingSum.update(input.time, input.value)
    val ret = rollingSum.current.value/window.count
    log.debug("计算SMA:" + ret)
    ret
  }
}

class ExponentialMovingAverage(pname: String, pperiod: Int, psmoothingFactor: Double) extends WindowIndicator[IndicatorDataPoint](pname, pperiod){
  private val _k: Double = {
    if(psmoothingFactor == Double.NaN) 2 / (period + 1)
    else psmoothingFactor
  }
  private val _period: Int = pperiod

  def this(pname: String, pperiod: Int) {
    this(pname, pperiod, Double.NaN)
  }

  def this(pperiod: Int) {
    this("EMA" + pperiod, pperiod)
  }

  def this(pperiod: Int, psmoothingFactor: Double) {
    this("EMA" + pperiod, pperiod, psmoothingFactor)
  }

  override def isReady: Boolean = samples >= period

  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = {
    if (samples == 1) input.value
    else input * _k + current *( 1 - _k);
  }
}

class LinearWeightedMovingAverage(pname: String, pperiod: Int) extends WindowIndicator[IndicatorDataPoint](pname, pperiod){

  def this (pperiod: Int) {
    this("LWMA" + pperiod, pperiod)
  }

  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = {
    var numerator: Double = 0.0
    var denominator: Long = 0

    for (i <- 0 to window.size) denominator += i

    // our first data point just return identity
    if (window.size == 1) input.value
    else {
      var index = window.size
      var minSizeSamples = math.min(window.size, window.samples).toInt
      for (i <- 0 to minSizeSamples) {
        index -= 1

        var x = window.get(i) * index
        numerator += x
      }
      numerator / denominator
    }
  }
}

class DoubleExponentialMovingAverage(pname: String, pperiod: Int, pvolumeFactor: Double = 1) extends IndicatorBase[IndicatorDataPoint](pname){
  private val _period: Int = pperiod
  private val _volumeFactor: Double = pvolumeFactor
  private val _ema1: ExponentialMovingAverage = new ExponentialMovingAverage(name + "_1", pperiod)
  private val _ema2: ExponentialMovingAverage = new ExponentialMovingAverage(name + "_2", pperiod)

  def this(pperiod: Int, pvolumeFactor:Double){
    this("DEMA" + pperiod, pperiod, pvolumeFactor)
  }
  def this(pperiod: Int){
    this(pperiod, 1)
  }

  override def isReady = samples > 2 *(_period -1)
  override def reset = {
    _ema1.reset
    _ema2.reset
    super.reset
  }

  override def computeNextValue(input: IndicatorDataPoint): Double = {
    _ema1.update(input);

    if (!_ema1.isReady) _ema1.current.value
    else {
      _ema2.update(_ema1.current)
      _ema1 * (_volumeFactor + 1) - _ema2 * _volumeFactor ;
    }

  }
}

class TripleExponentialMovingAverage(pname: String, pperiod: Int) extends WindowIndicator[IndicatorDataPoint](pname, pperiod){
  private val _period: Int = pperiod
  private val _ema1: ExponentialMovingAverage = new ExponentialMovingAverage(name + "_1", pperiod)
  private val _ema2: ExponentialMovingAverage = new ExponentialMovingAverage(name + "_2", pperiod)
  private val _ema3: ExponentialMovingAverage = new ExponentialMovingAverage(name + "_3", pperiod)
  
  def this(pperiod: Int) {
    this("TEMA" + pperiod, pperiod)
  }

  override def isReady: Boolean = samples > 3 * (_period - 1)

  override def reset: Unit = {
    _ema1.reset
    _ema2.reset
    _ema3.reset
    super.reset
  }

  override protected def computeNextValue(window: ReadOnlyWindow[IndicatorDataPoint], input: IndicatorDataPoint): Double = {
    _ema1.update(input)

    if (samples > _period - 1)
      _ema2.update(_ema1.current)

    if (samples > 2 * (_period - 1))
      _ema3.update(_ema2.current)

    if(isReady) _ema1 * 3 - _ema2 * 3 + _ema3 * 3 else 0
  }
}

class TriangularMovingAverage(pname: String, pperiod: Int) extends IndicatorBase[IndicatorDataPoint](pname){
  private val _period: Int = pperiod
  private val periodSma1 = if(pperiod % 2 == 0)  pperiod / 2  else (pperiod + 1) / 2
  private val periodSma2 = if(pperiod % 2 == 0) pperiod / 2 + 1 else (pperiod + 1) / 2;
  private val _sma1: SimpleMovingAverage = new SimpleMovingAverage(name + "_1", periodSma1)
  private val _sma2: SimpleMovingAverage = new SimpleMovingAverage(name + "_2", periodSma2)

  def this(pperiod: Int) {
    this("TRIMA" + pperiod, pperiod)
  }

  override def isReady: Boolean = samples >= _period
  override def reset = {
    _sma1.reset
    _sma2.reset
    super.reset
  }

  override def computeNextValue(input: IndicatorDataPoint): Double = {
    _sma1.update(input)
    _sma2.update(_sma1.current)
    _sma2.current.value
  }
}

class T3MovingAverage(pname: String, pperiod: Int, pvolumeFactor: Double = 0.7) extends IndicatorBase[IndicatorDataPoint](pname){
  private val _period: Int = pperiod
  private val _gd1: DoubleExponentialMovingAverage = new DoubleExponentialMovingAverage(name + "_1", pperiod, pvolumeFactor)
  private val _gd2: DoubleExponentialMovingAverage = new DoubleExponentialMovingAverage(name + "_2", pperiod, pvolumeFactor)
  private val _gd3: DoubleExponentialMovingAverage = new DoubleExponentialMovingAverage(name + "_3", pperiod, pvolumeFactor)

  def this(pperiod: Int, pvolumeFactor: Double) {
    this("T3(%d,%d)".format(pperiod, pvolumeFactor), pperiod, pvolumeFactor)
  }
  def this(pperiod: Int) {
    this(pperiod, 0.7 )
  }

  override def isReady: Boolean = samples >= 6*(_period-1)
  override def reset = {
    _gd1.reset
    _gd2.reset
    _gd3.reset
    super.reset
  }

  override def computeNextValue(input: IndicatorDataPoint): Double = {
    _gd1.update(input);

    if (!_gd1.isReady) _gd1.current.value
    else {
      _gd2.update(_gd1.current)
      if(!_gd2.isReady) _gd2.current.value
      else {
        _gd3.update(_gd2.current)
        _gd3.current.value
      }
    }
  }
}