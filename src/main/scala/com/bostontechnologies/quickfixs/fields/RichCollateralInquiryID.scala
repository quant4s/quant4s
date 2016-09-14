package com.bostontechnologies.quickfixs.fields

import quickfix.Message
import quickfix.field.CollInquiryID

trait RichCollateralInquiryID {

  val self: Message

  def hasCollateralInquiryId: Boolean = self.isSetField(CollInquiryID.FIELD)

  def collateralInquiryId: String = self.getString(CollInquiryID.FIELD)

  def collateralInquiryId_=(value: String) {
    self.setString(CollInquiryID.FIELD, value)
  }
}