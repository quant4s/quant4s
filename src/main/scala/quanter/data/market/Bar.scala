package quanter.data.market

/**
  *
  */
class Bar(popen: Double, pclose: Double, phigh: Double, plow: Double) extends TBar{
  open = popen
  close = pclose
  high = phigh
  low = plow

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
