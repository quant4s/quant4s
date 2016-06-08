package quanter

/**
  *
  */
object Asserts {
  def assert(check : => Boolean) = if(!check) throw new AssertionError()
}
