/**
  *
  */
package quanter.brokerages

import quanter.interfaces.TBrokerage

/**
  *
  */
abstract class  Brokerage(pname: String) extends TBrokerage{
  override def name = pname
}
