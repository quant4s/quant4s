package org.quant4s.indicators.window

import org.quant4s.indicators.IndicatorDataPoint

import scala.collection.immutable.Iterable

/**
  *
  */
trait ReadOnlyWindow[T] extends Iterable[T] {

  def size: Int
  def count: Int
  def samples: Double
  def isReady : Boolean
  def get(i:Int): T
  def set(i:Int, elem: T):Unit
  def mostRecentlyRemoved: T
  def indexOf(input: T): Int
}
