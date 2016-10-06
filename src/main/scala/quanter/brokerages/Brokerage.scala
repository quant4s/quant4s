/**
  *
  */
package quanter.brokerages

import quanter.interfaces.{TBrokerage, TLogging}

/**
  *
  */
abstract class  Brokerage(pname: String) extends TBrokerage with TLogging{
  def this() {
    this("")
  }


  override var name = pname
}
