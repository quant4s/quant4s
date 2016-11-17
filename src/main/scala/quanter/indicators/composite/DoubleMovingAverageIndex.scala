/**
  *
  */
package quanter.indicators.composite

import quanter.indicators.MovingAverageType.MovingAverageType
import quanter.indicators.{IndicatorDataPoint, MovingAverageType}
import quanter.indicators.IndicatorExtensions._

import scala.concurrent.duration.Duration

/** 双均线指标
  * 例如对一个指数采用MA5 和 MA15来计算， 指标的值为ma5 - ma15的值
  */
class DoubleMovingAverageIndex(pname: String, pshortPeriod: Int, plongPeriod: Int, maType: MovingAverageType )
  extends CompositeIndicator[IndicatorDataPoint](pname, maType.asIndicator(pname + "_SHORT", pshortPeriod),
    maType.asIndicator(pname + "_LONG", plongPeriod), (short, long) => short.current.value - long.current.value){
    def this(name: String, shortPeriod: Int, longPeriod: Int) {
        this(name, shortPeriod, longPeriod, MovingAverageType.Simple)
    }

    def this(shortPeriod: Int, longPeriod: Int, maType: MovingAverageType = MovingAverageType.Simple) {
        this("DMAI_%d_%d".format(shortPeriod, longPeriod), shortPeriod, longPeriod, maType)
    }

    def this(name: String, shortPeriod: Duration, longPeriod: Duration) {
        this(name, (shortPeriod.length / 1000000).toInt, (longPeriod.length /1000000).toInt, MovingAverageType.Simple)
    }
}
