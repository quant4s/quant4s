package quanter.consolidators

import quanter.Asserts
import quanter.data.{BaseData, TBaseData}

import scala.collection.mutable.ArrayBuffer

/**
  *
  */
trait TDataConsolidator[TInput <: TBaseData] {
  type EventHandler[TE] = (Object, TE) => Unit
  def consolidated: BaseData
  def workingData: BaseData
  var dataConsolidated = ArrayBuffer[EventHandler[BaseData]]()

  final def update(data: BaseData): Unit = {
    val typedData = data.asInstanceOf[TInput]
    Asserts.assert(typedData != null)

    update(typedData)
  }

  def update(data: TInput): Unit

}

abstract class DataConsolidator[TInput <: TBaseData] extends TDataConsolidator[TInput] {
  private var _consolidated: BaseData = null
  def consolidated: BaseData =_consolidated

  protected def onBaseDataConsolidated(data: BaseData) : Unit =  {
    if (dataConsolidated != null) {
      for(consolidater <- dataConsolidated)consolidater(this, data)
    }

    _consolidated = data
  }
}

