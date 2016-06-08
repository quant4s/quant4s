package quanter.data.market

import java.util.Date

import scala.collection.mutable.HashMap

/**
  *
  */
class DataDictionary[T](ptime: Date) extends HashMap[String, T] {
  var time: Date = ptime

  def this(data: Iterable[T], keySelector: T => String) {
    this(new Date(0))
    for(d <- data)
      this(keySelector(d)) = d
  }
}
