package com.bostontechnologies.quickfixs.messages

import java.util.Date
import quickfix.field._
import quickfix.Message
import java.math.BigDecimal
import quickfix.fix50.OrderCancelReplaceRequest

class RichOrderCancelReplaceRequest(self: Message) extends RichMessage(self) with InstrumentFields[Message] {

  require(RichMessage.isA(self, RichOrderCancelReplaceRequest.msgType))

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

  def hasSide: Boolean = self.isSetField(Side.FIELD)

  def side: Char = self.getChar(Side.FIELD)

  def side_=(value: Char) {
    self.setChar(Side.FIELD, value)
  }

  def hasOrderQty: Boolean = self.isSetField(OrderQty.FIELD)

  def orderQty: BigDecimal = self.getDecimal(OrderQty.FIELD)

  def orderQty_=(value: BigDecimal) {
    self.setDecimal(OrderQty.FIELD, value)
  }

  def orderQty_=(value: String) {
    orderQty = new BigDecimal(value)
  }

  def hasOrderType: Boolean = self.isSetField(OrdType.FIELD)

  def orderType: Char = self.getChar(OrdType.FIELD)

  def orderType_=(value: Char) {
    self.setChar(OrdType.FIELD, value)
  }

  def hasTransactTime: Boolean = self.isSetField(TransactTime.FIELD)

  def transactTime: Date = self.getUtcTimeStamp(TransactTime.FIELD)

  def transactTime_=(value: Date) {
    self.setUtcTimeStamp(TransactTime.FIELD, value, true)
  }

  def hasStopPrice: Boolean = self.isSetField(StopPx.FIELD)

  def stopPrice = self.getDecimal(StopPx.FIELD)

  def stopPrice_=(value: BigDecimal) {
    self.setDecimal(StopPx.FIELD, value)
  }

  def stopPrice_=(value: String) {
    stopPrice = new BigDecimal(value)
  }

  def hasPrice: Boolean = self.isSetField(Price.FIELD)

  def price: BigDecimal = self.getDecimal(Price.FIELD)

  def price_=(value: BigDecimal) {
    self.setDecimal(Price.FIELD, value)
  }

  def price_=(value: String) {
    price = new BigDecimal(value)
  }
}

object RichOrderCancelReplaceRequest extends RichMessageExtractor[RichOrderCancelReplaceRequest, OrderCancelReplaceRequest] {

  val msgType = MsgType.ORDER_CANCEL_REPLACE_REQUEST

  def apply(message: quickfix.fix50.OrderCancelReplaceRequest): RichOrderCancelReplaceRequest =
    new RichOrderCancelReplaceRequest(message)

  def new50Message: RichOrderCancelReplaceRequest = this(new quickfix.fix50.OrderCancelReplaceRequest)

  def apply(message: quickfix.fix44.OrderCancelReplaceRequest): RichOrderCancelReplaceRequest =
    new RichOrderCancelReplaceRequest(message)

  def new44Message: RichOrderCancelReplaceRequest = this(new quickfix.fix44.OrderCancelReplaceRequest)

  def newMessage: RichOrderCancelReplaceRequest = new RichOrderCancelReplaceRequest(RichMessage.newMessage(msgType).self)
}
