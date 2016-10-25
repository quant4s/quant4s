/**
  *
  */
package quanter.interfaces

import org.slf4j.LoggerFactory
import org.slf4s.Logger

/**
  *
  */
trait TLogging {
  val log = Logger(LoggerFactory.getLogger(this.getClass))
}
