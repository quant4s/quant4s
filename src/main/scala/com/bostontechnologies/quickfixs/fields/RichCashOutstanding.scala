package com.bostontechnologies.quickfixs.fields

import quickfix.Message
import quickfix.field.CashOutstanding

trait RichCashOutstanding {
  val self: Message

  def cashOutStanding = self.getDouble(CashOutstanding.FIELD)
  def cashOutStanding_=(value: Double) {self.setDouble(CashOutstanding.FIELD, value) }
}

