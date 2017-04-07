package org.quant4s.indicators.window

import org.slf4s.Logging
import org.quant4s.Asserts

import scala.collection.mutable.ArrayBuffer

/**
  * 一个允许通过列表访问的窗口，get(0) 表示第一个元素， get(count-1）表示最后一个元素
  */
class RollingWindow[T](private val capacity: Int) extends ReadOnlyWindow[T] with Logging{
  Asserts.assert(capacity >= 1)

  private var _tail = 0
  private var _mostRecentlyRemoved: T = _
  private var _samples: Int = 0
  val _items = new Array[Any](capacity)
  def add(item: T) = {
    // 如果容量小于元素个数, 那么 删除第一个
    if(_samples >= capacity) {
      _mostRecentlyRemoved = _items(_tail).asInstanceOf[T]
    }
    _items(_tail) = item
    _tail = (_tail +1) % capacity
    _samples += 1
  }

  def reset = {
    log.debug("invoke rolling window reset")
    _samples = 0
    _tail = 0
    //_items.clear()
   }

  def isReady = samples > size

  override def size: Int = capacity

  override def count: Int = {
    log.debug("window's count is min(%d, %d)".format(capacity, _samples))
    math.min(capacity, _samples)
  }
  override def samples: Double = _samples

  override def mostRecentlyRemoved: T = _mostRecentlyRemoved

  override def iterator: Iterator[T] =  {
    log.debug("invoke iterator, items count is %d".format(count))
    val temp = ArrayBuffer[T]()
    for(i <- 0 until count) temp :+ get(i)
    temp.iterator
  }

  override def get(i: Int): T = {
    Asserts.assert(i < size)
    _items((count + _tail - i - 1) % count).asInstanceOf[T]
  }

  override def toList: List[T] = {
    _items.toList.asInstanceOf[List[T]]
  }

  override def set(i: Int, elem: T): Unit = {
    Asserts.assert(i < size)
    _items((count + _tail - i - 1) % count) = elem
  }

  override def indexOf(elem: T): Int = {
    (_items.indexOf(elem) + capacity - _tail) % capacity
  }
}
