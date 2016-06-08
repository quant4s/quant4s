package quanter.consolidators

import quanter.data.market.TradeBar
import quanter.{Asserts, TimeSpan}
import quanter.data.{BaseData, TBaseData}

import scala.collection.mutable.ArrayBuffer
import quanter.CommonExtensions.DateExt

/**
  *
  */
abstract class PeriodCountConsolidatorBase[T <: TBaseData, TC <: BaseData](pmaxCount: Option[Int] = None, ptimespan: Option[TimeSpan] = None) extends DataConsolidator[T] {
  var b: Boolean = true//(pmaxCount == None) && (ptimespan == None)
  Asserts.assert(b)

  private val _maxCount = pmaxCount
  private val _period = ptimespan
  private var _currentCount: Int = 0
//  override var dataConsolidated = ArrayBuffer[EventHandler[TC]]()


  var _workingBar: TC = null.asInstanceOf[TC]
  override def workingData: BaseData =  {
    if(_workingBar != null)_workingBar else null
  }

  override def update(data: T): Unit = {
    if (shouldProcess(data)) {
      // 是否产生事件
      var fireDataConsolidated = false
      var aggregateBeforeFire = !_maxCount.isEmpty

      if(!_maxCount.isEmpty) {
        _currentCount += 1
        if(_currentCount >= _maxCount.get) {
          _currentCount = 0
          fireDataConsolidated = true
        }
      }

      if(_period != null) {
        if(_workingBar != null && data.time - _workingBar.time >= _period.getOrElse(TimeSpan.MinValue)) {
          fireDataConsolidated = true
        }
        //    // special case: always aggregate before event trigger when TimeSpan is zero
        //    if (_period.Value == TimeSpan.Zero)
        //    {
        //      fireDataConsolidated = true;
        //      aggregateBeforeFire = true;
        //    }
      }

      if(aggregateBeforeFire)
        aggregateBar(_workingBar, data)

      if(fireDataConsolidated) {
        val workingTradeBar = _workingBar.asInstanceOf[TradeBar]
        if(workingTradeBar != null) {

          onDataConsolidated(_workingBar)
          _workingBar = null.asInstanceOf[TC]
        }
      }

      if(!aggregateBeforeFire)
        _workingBar = aggregateBar(_workingBar, data)

    }

  }

  protected  def shouldProcess(data: T): Boolean = true

  protected def onDataConsolidated(e: TC): Unit = {
    dataConsolidated.foreach(h => h(this, e))
    super.onBaseDataConsolidated(e);
  }

  protected def aggregateBar(workingBar: TC, data: T): TC

}
