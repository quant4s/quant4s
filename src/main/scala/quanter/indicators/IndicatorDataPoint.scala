package quanter.indicators

import java.util.Date

import quanter.data.BaseData

/**
  *
  */
class IndicatorDataPoint(psymbol: String, ptime: Date, pvalue: Double) extends BaseData {
  value = pvalue
  time = ptime

  def this(ptime: Date, pvalue: Double) {
    this("", ptime, pvalue)
  }

  def this() {
    this(new Date(0), 0)
  }
}
