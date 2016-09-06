package quanter.indicators

import java.util.Date

import quanter.data.BaseData

/**
  * 一个特定时间点的数据
  */
class IndicatorDataPoint(psymbol: String, ptime: Date, pvalue: Double) extends BaseData {
  value = pvalue
  time = ptime
  symbol = psymbol

  def this(ptime: Date, pvalue: Double) {
    this("", ptime, pvalue)
  }

  def this() {
    this(new Date(0), 0)
  }

  override def toJson = """{"symbol":"%s", "value":"%d"}""".format(this.symbol, value)

}
