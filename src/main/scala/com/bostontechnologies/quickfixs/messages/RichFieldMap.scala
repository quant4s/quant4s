package com.bostontechnologies.quickfixs.messages

import quickfix.FieldMap
import scala.collection.JavaConversions._


trait RichFieldMap[T <: FieldMap] {
  val self: T

  override def toString: String = {
    val iter = self.iterator.map(_.toString)
    val fields = iter.reduceLeftOption {
      (xs, x) => xs + ", " + x
    } getOrElse ""
    this.getClass.getSimpleName + "(" + fields + ")"
  }
}
