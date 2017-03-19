package org.quant4s

/**
  *
  */
object Asserts {
  def assert(check : => Boolean) = if(!check) throw new AssertionError()
}
