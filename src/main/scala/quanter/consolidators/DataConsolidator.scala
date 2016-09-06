package quanter.consolidators

import quanter.Asserts
import quanter.data.{BaseData, TBaseData}

import scala.collection.mutable.ArrayBuffer

/**
  *
  */
trait TDataConsolidator {
  def consolidated: BaseData
  def workingData: BaseData

  def update(data: BaseData): Unit

}

abstract class DataConsolidator[TInput <: TBaseData] extends TDataConsolidator {
  type EventHandler[TE] = (Object, TE) => Unit
  var dataConsolidated = ArrayBuffer[EventHandler[BaseData]]()

  private var _consolidated: BaseData = null
  def consolidated: BaseData =_consolidated

  override def update(data: BaseData): Unit = {
    val typedData = data.asInstanceOf[TInput]
    Asserts.assert(typedData != null)

    update(typedData)
  }

  def update(data: TInput): Unit

  protected def onBaseDataConsolidated(data: BaseData) : Unit =  {
    if (dataConsolidated != null) {
      for(consolidater <- dataConsolidated)consolidater(this, data)
    }

    _consolidated = data
  }
}

