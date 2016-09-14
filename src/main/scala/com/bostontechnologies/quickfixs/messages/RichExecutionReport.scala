package com.bostontechnologies.quickfixs.messages

import quickfix.Message
import quickfix.field._
import java.util.Date
import java.math.BigDecimal
import quickfix.fix50.ExecutionReport

class RichExecutionReport private(self: Message) extends RichMessage(self) with InstrumentFields[Message] {

  require(RichMessage.isA(self, RichExecutionReport.msgType))

  def hasOrderType: Boolean = self.isSetField(OrdType.FIELD)

  def ordType: Char = self.getChar(OrdType.FIELD)

  def ordType_=(value: Char) {
    self.setChar(OrdType.FIELD, value)
  }

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

  def hasExecId: Boolean = self.isSetField(ExecID.FIELD)

  def execId: String = self.getString(ExecID.FIELD)

  def execId_=(value: String) {
    self.setString(ExecID.FIELD, value)
  }

  def hasExecType: Boolean = self.isSetField(ExecType.FIELD)

  def execType: Char = self.getChar(ExecType.FIELD)

  def execType_=(value: Char) {
    self.setChar(ExecType.FIELD, value)
  }

  def hasOrdStatus: Boolean = self.isSetField(OrdStatus.FIELD)

  def ordStatus: Char = self.getChar(OrdStatus.FIELD)

  def ordStatus_=(value: Char) {
    self.setChar(OrdStatus.FIELD, value)
  }

  def hasOrdRejReason: Boolean = self.isSetField(OrdRejReason.FIELD)

  def ordRejReason: Int = self.getInt(OrdRejReason.FIELD)

  def ordRejReason_=(value: Int) {
    self.setInt(OrdRejReason.FIELD, value)
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

  def hasAvgPrice: Boolean = self.isSetField(AvgPx.FIELD)

  def avgPrice: BigDecimal = self.getDecimal(AvgPx.FIELD)

  def avgPrice_=(value: BigDecimal) {
    self.setDecimal(AvgPx.FIELD, value)
  }

  def avgPrice_=(value: String) {
    avgPrice = new BigDecimal(value)
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

  def hasLeavesQty: Boolean = self.isSetField(LeavesQty.FIELD)

  def leavesQty: BigDecimal = self.getDecimal(LeavesQty.FIELD)

  def leavesQty_=(value: BigDecimal) {
    self.setDecimal(LeavesQty.FIELD, value)
  }

  def leavesQty_=(value: String) {
    leavesQty = new BigDecimal(value)
  }

  def hasCumQty: Boolean = self.isSetField(CumQty.FIELD)

  def cumQty: BigDecimal = self.getDecimal(CumQty.FIELD)

  def cumQty_=(value: BigDecimal) {
    self.setDecimal(CumQty.FIELD, value)
  }

  def cumQty_=(value: String) {
    cumQty = new BigDecimal(value)
  }

  def hasTransactTime: Boolean = self.isSetField(TransactTime.FIELD)

  def transactTime: Date = self.getUtcTimeStamp(TransactTime.FIELD)

  def transactTime_=(value: Date) {
    self.setUtcTimeStamp(TransactTime.FIELD, value, true)
  }

  def hasText: Boolean = self.isSetField(Text.FIELD)

  def text: String = self.getString(Text.FIELD)

  def text_=(value: String) {
    self.setString(Text.FIELD, value)
  }

  def hasQuoteId: Boolean = self.isSetField(QuoteID.FIELD)

  def quoteId: String = self.getString(QuoteID.FIELD)

  def quoteId_=(value: String) {
    self.setString(QuoteID.FIELD, value)
  }

  def hasCurrency: Boolean = self.isSetField(Currency.FIELD)

  def currency: String = self.getString(Currency.FIELD)

  def currency_=(value: String) {
    self.setString(Currency.FIELD, value)
  }

  def hasBookingType: Boolean = self.isSetField(BookingType.FIELD)

  def bookingType: Int = self.getInt(BookingType.FIELD)

  def bookingType_=(value: Int) {
    self.setInt(BookingType.FIELD, value)
  }
}

object RichExecutionReport extends RichMessageExtractor[RichExecutionReport, ExecutionReport] {

  val msgType = MsgType.EXECUTION_REPORT

  def apply(message: quickfix.fix50.ExecutionReport): RichExecutionReport =
    new RichExecutionReport(message)

  def new50Message: RichExecutionReport = this(new quickfix.fix50.ExecutionReport)

  def apply(message: quickfix.fix44.ExecutionReport): RichExecutionReport =
    new RichExecutionReport(message)

  def apply(message: quickfix.fix42.ExecutionReport): RichExecutionReport =
    new RichExecutionReport(message)

  def new44Message: RichExecutionReport = this(new quickfix.fix44.ExecutionReport)

  def newMessage: RichExecutionReport = new RichExecutionReport(RichMessage.newMessage(msgType).self)
}
