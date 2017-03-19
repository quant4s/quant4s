package org.quant4s.consolidators

import java.util.Date

import org.quant4s.TimeSpan
import org.quant4s.data.{BaseData, TBaseData}
import org.quant4s.data.market.TradeBar
import org.quant4s.Asserts
import org.quant4s.data.TBaseData

import scala.collection.mutable.ArrayBuffer
import org.quant4s.CommonExtensions.DateExt

/**
  *
  */
abstract class PeriodCountConsolidatorBase[T <: TBaseData, TC <: BaseData](pmaxCount: Option[Int] = None, ptimespan: Option[TimeSpan] = None) extends DataConsolidator[T] {
  Asserts.assert((pmaxCount.isDefined) || (ptimespan.isDefined))

  private val _maxCount = pmaxCount
  private val _period = ptimespan
  private var _currentCount: Int = 0
  private var _lastEmit: Option[Date] = None
//  override var dataConsolidated = ArrayBuffer[EventHandler[TC]]()


  var _workingBar: TC = null.asInstanceOf[TC]
  override def workingData: BaseData =  {
    // TODO: 返回值 用_workingBar.clone 替代 _workingBar
    if(_workingBar != null.asInstanceOf[TC]) _workingBar else null
  }

  override def update(data: T): Unit = {
    if (shouldProcess(data)) {
      // 是否产生事件
      var fireDataConsolidated = false
      var aggregateBeforeFire = _maxCount.isDefined

      if(aggregateBeforeFire) {
        _currentCount += 1
        if(_currentCount >= _maxCount.get) {
          _currentCount = 0
          fireDataConsolidated = true
        }
      }

      if(_lastEmit.isEmpty) _lastEmit = Some(data.time)
      if(_period.isDefined) {
        if(_workingBar != null && (data.time - _workingBar.time) >= _period.getOrElse(TimeSpan.minValue)) {
          fireDataConsolidated = true
        }

        // special case: always aggregate before event trigger when TimeSpan is zero
        if (_period.get == TimeSpan.zero)
        {
          fireDataConsolidated = true
          aggregateBeforeFire = true
        }
      }

      if(aggregateBeforeFire)
        _workingBar = aggregateBar(_workingBar, data)

      if(fireDataConsolidated) {
        val workingTradeBar = _workingBar.asInstanceOf[TradeBar]
        if(workingTradeBar != null) {
          if(_period.isDefined) {
            workingTradeBar.period = _period.get
          }else if(data.isInstanceOf[TradeBar]) {
            workingTradeBar.period = TimeSpan.fromTicks(data.time.getTime() - _lastEmit.get.getTime())
          }
        }
        onDataConsolidated(_workingBar)
        _lastEmit = Some(data.time)
        _workingBar = null.asInstanceOf[TC]
      }

      if(!aggregateBeforeFire)
        _workingBar = aggregateBar(_workingBar, data)
    }
  }

  protected  def shouldProcess(data: T): Boolean = true

  protected def onDataConsolidated(e: TC): Unit = {
    // dataConsolidated.foreach(h => h(this, e))
    super.onBaseDataConsolidated(e)
  }

  protected def aggregateBar(workingBar: TC, data: T): TC

  protected def getRoundedBarTime(time: Date): Date = {
    if (!_period.isEmpty && _maxCount.isEmpty) time.roundDown(_period.get)
    else time
  }
}
