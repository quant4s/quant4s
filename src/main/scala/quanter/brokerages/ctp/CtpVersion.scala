/**
  *
  */
package quanter.brokerages.ctp

sealed trait CtpVersion
trait Ctp6_3_6 extends CtpVersion

object CtpVersion {
  implicit object Ctp6_3_6 extends Ctp6_3_6
}

