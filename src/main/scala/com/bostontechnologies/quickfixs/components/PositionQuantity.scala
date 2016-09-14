package com.bostontechnologies.quickfixs.components

import java.math.BigDecimal
import java.util.Date
import quickfix.{Message, FieldMap}
import quickfix.field._

case class PositionQuantity(positionType: String, longQuantity: BigDecimal, shortQuantity: BigDecimal,
                            quantityDate: Date, positionQuantityStatus: Int = PosQtyStatus.ACCEPTED) {
  def toFields: FieldMap = {
    val fields = new Message
    fields.setString(PosType.FIELD, positionType)
    fields.setDecimal(LongQty.FIELD, longQuantity)
    fields.setDecimal(ShortQty.FIELD, shortQuantity)
    fields.setInt(PosQtyStatus.FIELD, positionQuantityStatus)
    fields.setUtcDateOnly(QuantityDate.FIELD, quantityDate)

    fields
  }
}

object PositionQuantity {

  def apply(fieldMap: FieldMap): PositionQuantity = PositionQuantity(fieldMap.getString(PosType.FIELD),
                                                                     fieldMap.getDecimal(LongQty.FIELD),
                                                                     fieldMap.getDecimal(ShortQty.FIELD),
                                                                     fieldMap.getUtcDateOnly(QuantityDate.FIELD),
                                                                     fieldMap.getInt(PosQtyStatus.FIELD))
}