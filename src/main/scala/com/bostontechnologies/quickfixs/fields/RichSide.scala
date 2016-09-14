package com.bostontechnologies.quickfixs.fields

import quickfix.Message
import quickfix.field.{Side => QFSide}

trait RichSide {
  val self: Message

  def hasSide: Boolean = self.isSetField(QFSide.FIELD)

  def side: Char = self.getChar(QFSide.FIELD)

  def side_=(value: Char) {
    self.setChar(QFSide.FIELD, value)
  }

}