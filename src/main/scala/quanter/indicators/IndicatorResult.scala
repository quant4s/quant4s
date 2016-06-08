package quanter.indicators

import quanter.indicators.IndicatorStatus.IndicatorStatus

/**
  *
  */
class IndicatorResult(val value: Double, val status: IndicatorStatus = IndicatorStatus.Success) {
}
