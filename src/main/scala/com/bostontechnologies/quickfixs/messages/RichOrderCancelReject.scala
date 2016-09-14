package com.bostontechnologies.quickfixs.messages

import quickfix.Message
import quickfix.field._
import quickfix.fix50.OrderCancelReject

class RichOrderCancelReject private(self: Message) extends RichMessage(self) {

  require(RichMessage.isA(self, RichOrderCancelReject.msgType))

  def hasOrderId: Boolean = self.isSetField(OrderID.FIELD)

  def orderId: String = self.getString(OrderID.FIELD)

  def orderId_=(value: String) {
    self.setString(OrderID.FIELD, value)
  }

  def hasOrigClOrdId: Boolean = self.isSetField(OrigClOrdID.FIELD)

  def origClOrdId: String = self.getString(OrigClOrdID.FIELD)

  def origClOrdId_=(value: String) {
    self.setString(OrigClOrdID.FIELD, value)
  }

  def hasClOrdId: Boolean = self.isSetField(ClOrdID.FIELD)

  def clOrdId: String = self.getString(ClOrdID.FIELD)

  def clOrdId_=(value: String) {
    self.setString(ClOrdID.FIELD, value)
  }

  def hasOrdStatus: Boolean = self.isSetField(OrdStatus.FIELD)

  def ordStatus: Char = self.getChar(OrdStatus.FIELD)

  def ordStatus_=(value: Char) {
    self.setChar(OrdStatus.FIELD, value)
  }

  def hasCancelRejectResponseTo: Boolean = self.isSetField(CxlRejResponseTo.FIELD)

  def cancelRejectResponseTo: Char = self.getChar(CxlRejResponseTo.FIELD)

  def cancelRejectResponseTo_=(value: Char) {
    self.setChar(CxlRejResponseTo.FIELD, value)
  }

  def hasCancelRejectReason: Boolean = self.isSetField(CxlRejReason.FIELD)

  def cancelRejectReason: Int = self.getInt(CxlRejReason.FIELD)

  def cancelRejectReason_=(value: Int) {
    self.setInt(CxlRejReason.FIELD, value)
  }

  def hasText: Boolean = self.isSetField(Text.FIELD)

  def text: String = self.getString(Text.FIELD)

  def text_=(value: String) {
    self.setString(Text.FIELD, value)
  }
}

object RichOrderCancelReject extends RichMessageExtractor[RichOrderCancelReject, OrderCancelReject] {

  val msgType = MsgType.ORDER_CANCEL_REJECT

  def apply(message: quickfix.fix50.OrderCancelReject): RichOrderCancelReject =
    new RichOrderCancelReject(message)

  def new50Message: RichOrderCancelReject = this(new quickfix.fix50.OrderCancelReject)

  def apply(message: quickfix.fix44.OrderCancelReject): RichOrderCancelReject =
    new RichOrderCancelReject(message)

  def new44Message: RichOrderCancelReject = this(new quickfix.fix44.OrderCancelReject)

  def newMessage: RichOrderCancelReject = new RichOrderCancelReject(RichMessage.newMessage(msgType).self)
}