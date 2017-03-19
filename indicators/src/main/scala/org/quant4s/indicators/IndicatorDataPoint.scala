package org.quant4s.indicators

import java.util.Date

import org.quant4s.data.BaseData

/**
  * 一个特定时间点的数据
  */
class IndicatorDataPoint(psymbol: String, ptime: Date, pvalue: Double) extends BaseData with Comparable[IndicatorDataPoint] {
  value = pvalue
  time = ptime
  symbol = psymbol

  def this(ptime: Date, pvalue: Double) {
    this("", ptime, pvalue)
  }

  def this() {
    this(new Date(0), 0)
  }

  override def compareTo(o: IndicatorDataPoint): Int = {
    if( o == null) 1
    else value.compareTo(o.value)
  }
}
