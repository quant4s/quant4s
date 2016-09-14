package com.bostontechnologies.quickfixs.fields

import quickfix.Message
import quickfix.field.TotalNetValue

trait RichTotalNetValue {
  val self: Message

  def hasTotalNetValue = self.isSetField(TotalNetValue.FIELD)
  def totalNetValue_=(value: Double) {self.setDouble(TotalNetValue.FIELD, value)}
  def totalNetValue = self.getDouble(TotalNetValue.FIELD)
}