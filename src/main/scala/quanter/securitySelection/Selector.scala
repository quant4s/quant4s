package quanter.securitySelection

/**
  *
  */
sealed case class Selector(val pool: List[Instrument]) {
  def intersect(selector: Selector): Selector = new Selector(pool.intersect(selector.pool))

  def filter(cmp: Instrument => Boolean): Selector = new Selector(pool.filter(cmp))

}
