package com.bostontechnologies.quickfixs.messages

import quickfix.Message
import quickfix.field._
import java.util.Date
import java.math.BigDecimal
import com.bostontechnologies.quickfixs.fields.{RichSide, RichAccount}
import quickfix.fix50.NewOrderSingle

class RichNewOrderSingle private(self: Message)
    extends RichMessage(self)
    with InstrumentFields[Message]
    with RichAccount
    with RichSide {

	require(RichMessage.isA(self, RichNewOrderSingle.msgType))

	def hasClOrdId: Boolean = self.isSetField(ClOrdID.FIELD)

  def clOrdId: String = self.getString(ClOrdID.FIELD)

  def clOrdId_=(value: String) {
    self.setString(ClOrdID.FIELD, value)
  }

  def hasCurrency: Boolean = self.isSetField(Currency.FIELD)

  def currency: String = self.getString(Currency.FIELD)

  def currency_=(value: String) {
   self.setString(Currency.FIELD, value)
  }

  def hasOrderQty: Boolean = self.isSetField(OrderQty.FIELD)

  def orderQty: BigDecimal = self.getDecimal(OrderQty.FIELD)

  def orderQty_=(value: BigDecimal) {
    self.setDecimal(OrderQty.FIELD, value)
  }

  def orderQty_=(value: String) {
    orderQty = new BigDecimal(value)
  }

  def hasTransactTime: Boolean = self.isSetField(TransactTime.FIELD)

  def transactTime: Date = self.getUtcTimeStamp(TransactTime.FIELD)

  def transactTime_=(value: Date) {
    self.setUtcTimeStamp(TransactTime.FIELD, value, true)
  }

  def hasOrderType: Boolean = self.isSetField(OrdType.FIELD)

  def orderType: Char = self.getChar(OrdType.FIELD)

  def orderType_=(value: Char) {
    self.setChar(OrdType.FIELD, value)
  }

  def hasPrice: Boolean = self.isSetField(Price.FIELD)

  def price: BigDecimal = self.getDecimal(Price.FIELD)

  def price_=(value: BigDecimal) {
    self.setDecimal(Price.FIELD, value)
  }

  def price_=(value: String) {
    price = new BigDecimal(value)
  }

  def hasStopPrice: Boolean = self.isSetField(StopPx.FIELD)

  def stopPrice = self.getDecimal(StopPx.FIELD)

  def stopPrice_=(value: BigDecimal) {
    self.setDecimal(StopPx.FIELD, value)
  }

  def stopPrice_=(value: String) {
    stopPrice = new BigDecimal(value)
  }

  def hasQuoteId: Boolean = self.isSetField(QuoteID.FIELD)

  def quoteId: String = self.getString(QuoteID.FIELD)

  def quoteId_=(value: String) {
    self.setString(QuoteID.FIELD, value)
  }

  def hasRefOrderId: Boolean = self.isSetField(RefOrderID.FIELD)

  def refOrderId: String = self.getString(RefOrderID.FIELD)

  def refOrderId_=(value: String) {
    self.setString(RefOrderID.FIELD, value)
  }

  def handlInst: Char = self.getChar(HandlInst.FIELD)

  def handlInst_=(value: Char) {
    self.setChar(HandlInst.FIELD, value)
  }

  def hasTimeInForce: Boolean = self.isSetField(TimeInForce.FIELD)

  def timeInForce: Char = self.getChar(TimeInForce.FIELD)

  def timeInForce_=(value: Char) {
    self.setChar(TimeInForce.FIELD, value)
  }

  def hasBookingType: Boolean = self.isSetField(BookingType.FIELD)

  def bookingType: Int = self.getInt(BookingType.FIELD)

  def bookingType_=(value: Int) {
    self.setInt(BookingType.FIELD, value)
  }

}

object RichNewOrderSingle extends RichMessageExtractor[RichNewOrderSingle, NewOrderSingle] {

	val msgType = MsgType.ORDER_SINGLE

	def apply(message: quickfix.fix50.NewOrderSingle): RichNewOrderSingle =
		new RichNewOrderSingle(message)

	def new50Message: RichNewOrderSingle = apply(new quickfix.fix50.NewOrderSingle)

	def apply(message: quickfix.fix44.NewOrderSingle): RichNewOrderSingle =
		new RichNewOrderSingle(message)

	def new44Message: RichNewOrderSingle = apply(new quickfix.fix44.NewOrderSingle)

  def apply(message: quickfix.fix42.NewOrderSingle): RichNewOrderSingle =
    new RichNewOrderSingle(message)

  def new42Message: RichNewOrderSingle = apply(new quickfix.fix42.NewOrderSingle)

	def newMessage: RichNewOrderSingle = new RichNewOrderSingle(RichMessage.newMessage(msgType).self)
}
