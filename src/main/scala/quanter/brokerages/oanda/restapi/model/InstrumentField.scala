/**
  *
  */
package quanter.brokerages.oanda.restapi.model

sealed trait InstrumentField

object InstrumentField {

  case object Instrument extends InstrumentField {
    override def toString = "instrument"
  }

  case object DisplayName extends InstrumentField {
    override def toString = "displayName"
  }

  case object Pip extends InstrumentField {
    override def toString = "pip"
  }

  case object MaxTradeUnits extends InstrumentField {
    override def toString = "maxTradeUnits"
  }

  case object Precision extends InstrumentField {
    override def toString = "precision"
  }

  case object MaxTrailingStop extends InstrumentField {
    override def toString = "maxTrailingStop"
  }

  case object MinTrailingStop extends InstrumentField {
    override def toString = "minTrailingStop"
  }

  case object MarginRate extends InstrumentField {
    override def toString = "marginRate"
  }

  case object Halted extends InstrumentField {
    override def toString = "halted"
  }

}
