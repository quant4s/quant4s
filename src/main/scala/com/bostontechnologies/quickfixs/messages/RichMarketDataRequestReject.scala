package com.bostontechnologies.quickfixs.messages

import java.lang.Boolean
import quickfix.field.{MsgType, Text, MDReqRejReason, MDReqID}
import quickfix.Message
import quickfix.fix50.MarketDataRequestReject

class RichMarketDataRequestReject private(self: Message) extends RichMessage(self) {

	require(RichMessage.isA(self, RichMarketDataRequestReject.msgType))

  def hasRequestId: Boolean = self.isSetField(MDReqID.FIELD)

  def requestId: String = self.getString(MDReqID.FIELD)

  def requestId_=(requestId: String) {
    self.setString(MDReqID.FIELD, requestId)
  }

  def hasRejectReason: Boolean = self.isSetField(MDReqRejReason.FIELD)

  def rejectReason = self.getChar(MDReqRejReason.FIELD)

  def rejectReason_=(value: Char) {
    self.setChar(MDReqRejReason.FIELD, value)
  }

  def hasText: Boolean = self.isSetField(Text.FIELD)

  def text = self.getString(Text.FIELD)

  def text_=(value: String) {
    self.setString(Text.FIELD, value)
  }

}

object RichMarketDataRequestReject extends RichMessageExtractor[RichMarketDataRequestReject, MarketDataRequestReject] {

	val msgType = MsgType.MARKET_DATA_REQUEST_REJECT

	def apply(message: quickfix.fix50.MarketDataRequestReject): RichMarketDataRequestReject =
		new RichMarketDataRequestReject(message)

	def new50Message: RichMarketDataRequestReject = this(new quickfix.fix50.MarketDataRequestReject)

	def apply(message: quickfix.fix44.MarketDataRequestReject): RichMarketDataRequestReject =
		new RichMarketDataRequestReject(message)

	def new44Message: RichMarketDataRequestReject = this(new quickfix.fix44.MarketDataRequestReject)

  def apply(message: quickfix.fix42.MarketDataRequestReject): RichMarketDataRequestReject =
  		new RichMarketDataRequestReject(message)

	def newMessage: RichMarketDataRequestReject = new RichMarketDataRequestReject(RichMessage.newMessage(msgType).self)

  def apply(requestId: String, rejectReason: Char, text: String = ""): RichMarketDataRequestReject = {
    val message = newMessage
    message.requestId = requestId
    message.rejectReason = rejectReason
    if (!text.isEmpty) {
      message.text = text
    }
    message
  }
}
