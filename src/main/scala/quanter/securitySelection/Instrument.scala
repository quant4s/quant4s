/**
  *
  */
package quanter.securitySelection

/**
  *
  */
class Instrument(val code: String, val pe: Double, val pb: Double, val capital: Long, val roe: Double, val mv: Long) {
  // def marketValue(price: Double): Long = (capital * price).toLong
}
