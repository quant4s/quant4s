package org.quant4s.data.market

/**
  *
  */
class Bar(popen: Double, pclose: Double, phigh: Double, plow: Double) extends TBar{
  private var _open: Double = 0
  private var _close: Double = 0
  private var _high: Double = 0
  private var _low: Double = 0

  override def open: Double = _open
  def open_=(newValue: Double) {
    _open = newValue
  }

  override def high: Double = _high
  def high_=(newValue: Double)  {
    _high = newValue
  }

  override def low: Double = _low
  def low_=(newValue: Double)  {
    _low = newValue
  }
  override def close: Double = _close
  def close_=(newValue: Double)  {
    _close = newValue
  }

  def this() {
    this(0,0,0,0)
  }

  def update(value: Double) = {
    if(open == 0) {
      open = value
      high = value
      low = value
    }
    if(value > high) high = value
    if(value < low) low = value
    close = value
  }
}
