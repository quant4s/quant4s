package org.quant4s.consolidators

import org.quant4s.data.{BaseData, TBaseData}
import org.quant4s.Asserts
import org.quant4s.data.TBaseData

import scala.collection.mutable.ArrayBuffer

/**
  *
  */
trait TDataConsolidator {
  def consolidated: BaseData
  def workingData: BaseData

  def update(data: BaseData): Unit
  type EventHandler[BaseData] = (Any, BaseData) => Unit
  var dataConsolidated = ArrayBuffer[EventHandler[BaseData]]()

}

abstract class DataConsolidator[TInput <: TBaseData] extends TDataConsolidator {

  private var _consolidated: BaseData = null
  def consolidated: BaseData =_consolidated

  override def update(data: BaseData): Unit = {
    val typedData = data.asInstanceOf[TInput]
    Asserts.assert(typedData != null)

    update(typedData)
  }

  def update(data: TInput): Unit

  protected def onBaseDataConsolidated(data: BaseData) : Unit =  {
//    if (dataConsolidated != null) {
//      for(consolidater <- dataConsolidated) consolidater(this, data)
//    }
    dataConsolidated.foreach(h => h(this, data))
    _consolidated = data
  }
}

