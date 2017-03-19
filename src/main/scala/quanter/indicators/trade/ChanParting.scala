package quanter.indicators.trade

import org.quant4s.data.market.TradeBar
import quanter.indicators.TradeBarIndicator

import scala.collection.mutable.ArrayBuffer

/**
  *
  */
class ChanParting(pname: String) extends TradeBarIndicator(pname){

  override def isReady: Boolean = samples > 0
  private var _bars = ArrayBuffer[Bar]()

  def this() {
    this("PARTING")
  }
  /*
  * 0: 包含 1 顶分型  2 底分型 -1 趋势
  * */
  override def computeNextValue(input: TradeBar): Double = {
    val ret = _preProcessBar(input)
    if (ret) 0 // 被包含
    else _topParting
  }

  override def reset: Unit = {
    _bars.clear()
    super.reset
  }

  /*
    * 处理了bar的包含关系
    * */
  private def _preProcessBar(input: TradeBar): Boolean = {
    if(_bars.size <= 0) {
      _bars += new Bar() {
        high = input.high
        low = input.low
      }
      false
    } else {
      val bar = _bars(_bars.size - 1)
      if ((bar.high > input.high && bar.low < input.low) || (bar.high < input.high && bar.low > input.low)) {
        // 发现包含
        if (_isAscending) {
          bar.high = math.max(bar.high, input.high)
          bar.low = math.max(bar.low, input.low)
        } else {
          bar.high = math.min(bar.high, input.high)
          bar.low = math.min(bar.low, input.low)
        }
        true
      }
      else { // 未包含，新增
        _bars += new Bar() {
          high = input.high
          low = input.low
        }
        if(_bars.size > 3) // 保持 3个bar数据
          _bars.remove(0)
        false
      }
    }
  }

  /*
  * 是否上升趋势
  * */
  private def _isAscending:Boolean = {
    if(_bars.size <= 1) true
    else if(_bars(_bars.size-2).high < _bars(_bars.size -1).high) true
    else false
  }

  /*
  * 1 顶分型  2 底分型 -1 趋势
  * */
  private def _topParting: Int = {
    if(_bars.size == 3) {
      // 中间一根
      if ((_bars(1).high > _bars(0).high && _bars(1).high > _bars(2).high) &&
        (_bars(1).low > _bars(0).low && _bars(1).low > _bars(2).low))
        1
      else if ((_bars(1).high < _bars(0).high && _bars(1).high < _bars(2).high) &&
        (_bars(1).low < _bars(0).low && _bars(1).low < _bars(2).low))
        2
      else -1
    } else {
      -1
    }
  }


  private class Bar {
    var high: Double = 0
    var low: Double = 0
  }
}
