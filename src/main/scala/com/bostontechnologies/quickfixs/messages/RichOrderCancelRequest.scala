package com.bostontechnologies.quickfixs.messages

import java.util.Date
import quickfix.field._
import quickfix.Message
import java.math.BigDecimal
import com.bostontechnologies.quickfixs.fields.RichSide
import quickfix.fix50.OrderCancelRequest

class RichOrderCancelRequest(self: Message) extends RichMessage(self) with InstrumentFields[Message] with RichSide {

	require(RichMessage.isA(self, RichOrderCancelRequest.msgType))

  def hasOrigClOrdId: Boolean = self.isSetField(OrigClOrdID.FIELD)

	def origClOrdId: String = self.getString(OrigClOrdID.FIELD)

	def origClOrdId_=(value: String){
    self.setString(OrigClOrdID.FIELD, value)
  }

  def hasClOrdId: Boolean = self.isSetField(ClOrdID.FIELD)

	def clOrdId: String = self.getString(ClOrdID.FIELD)

	def clOrdId_=(value: String) {
    self.setString(ClOrdID.FIELD, value)
  }

  def hasOrderQty: Boolean = self.isSetField(OrderQty.FIELD)

	def orderQty: BigDecimal = self.getDecimal(OrderQty.FIELD)

	def orderQty_=(value: BigDecimal){
    self.setDecimal(OrderQty.FIELD, value)
  }

	def orderQty_=(value: String){
    orderQty = new BigDecimal(value)
  }

  def hasTransactTime: Boolean = self.isSetField(TransactTime.FIELD)

	def transactTime: Date = self.getUtcTimeStamp(TransactTime.FIELD)

  def transactTime_=(value: Date){
    self.setUtcTimeStamp(TransactTime.FIELD, value, true)
  }

}

object RichOrderCancelRequest extends RichMessageExtractor[RichOrderCancelRequest, OrderCancelRequest] {

	val msgType = MsgType.ORDER_CANCEL_REQUEST

	def apply(message: quickfix.fix50.OrderCancelRequest): RichOrderCancelRequest =
		new RichOrderCancelRequest(message)

	def new50Message: RichOrderCancelRequest = this(new quickfix.fix50.OrderCancelRequest)

	def apply(message: quickfix.fix44.OrderCancelRequest): RichOrderCancelRequest =
		new RichOrderCancelRequest(message)

	def new44Message: RichOrderCancelRequest = this(new quickfix.fix44.OrderCancelRequest)

	def newMessage: RichOrderCancelRequest = new RichOrderCancelRequest(RichMessage.newMessage(msgType).self)
}
