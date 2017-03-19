package org.quant4s.indicators

import org.quant4s.indicators.IndicatorStatus.IndicatorStatus

/**
  *
  */
class IndicatorResult(val value: Double, val status: IndicatorStatus = IndicatorStatus.Success) {
}
