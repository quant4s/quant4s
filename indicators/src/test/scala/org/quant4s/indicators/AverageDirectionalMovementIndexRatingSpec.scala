/**
  *
  */
package org.quant4s.indicators

import org.quant4s.data.market.TradeBar

/**
  *
  */
class AverageDirectionalMovementIndexRatingSpec extends CommonIndicatorSpec[TradeBar]{
  epsilon = 1.0
  override protected def createIndicator: IndicatorBase[TradeBar] = new AverageDirectionalMovementIndexRating(14)

  override protected def testColumnName: String = "ADXR_14"

  override protected def testFileName: String = "spy_adxr.txt"
}
