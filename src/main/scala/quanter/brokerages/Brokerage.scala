/**
  *
  */
package quanter.brokerages

import quanter.interfaces.TBrokerage

/**
  *
  */
abstract class  Brokerage(pname: String) extends TBrokerage{
  def this() {
    this("")
  }

  override var name = pname
}
