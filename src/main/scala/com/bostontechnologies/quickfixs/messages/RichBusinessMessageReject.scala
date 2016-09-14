package com.bostontechnologies.quickfixs.messages

import quickfix.Message
import quickfix.fix50.BusinessMessageReject
import quickfix.field._

class RichBusinessMessageReject private(self: Message)
  extends RichMessage(self) {

  require(RichMessage.isA(self, RichBusinessMessageReject.msgType))

  def hasText: Boolean = self.isSetField(Text.FIELD)

  def text: String = self.getString(Text.FIELD)

  def text_=(value: String) {
    self.setString(Text.FIELD, value)
  }

  def refMessageType: String = self.getString(RefMsgType.FIELD)

  def refMessageType_=(value: String) {
    self.setString(RefMsgType.FIELD, value)
  }

  def refMessageSequenceNumber: Int = self.getInt(RefSeqNum.FIELD)

  def refMessageSequenceNumber_=(value: Int) {
    self.setInt(RefSeqNum.FIELD, value)
  }

  def businessRejectReason: Int = self.getInt(BusinessRejectReason.FIELD)

  def businessRejectReason_=(value: Int) {
    self.setInt(BusinessRejectReason.FIELD, value)
  }

  def hasRefTag: Boolean = self.isSetField(BusinessRejectRefID.FIELD)

  def refTag: Int = self.getInt(BusinessRejectRefID.FIELD)

  def refTag_=(value: Int) {
    self.setInt(BusinessRejectRefID.FIELD, value)
  }
}

object RichBusinessMessageReject extends RichMessageExtractor[RichBusinessMessageReject, BusinessMessageReject] {

  val msgType = MsgType.BUSINESS_MESSAGE_REJECT

  def apply(message: quickfix.fix50.BusinessMessageReject): RichBusinessMessageReject =
    new RichBusinessMessageReject(message)

  def new50Message: RichBusinessMessageReject = this(new quickfix.fix50.BusinessMessageReject())

  def apply(message: quickfix.fix44.BusinessMessageReject): RichBusinessMessageReject =
    new RichBusinessMessageReject(message)

  def new44Message: RichBusinessMessageReject = this(new quickfix.fix44.BusinessMessageReject())

  def newMessage: RichBusinessMessageReject = new RichBusinessMessageReject(RichMessage.newMessage(msgType).self)

  def apply(request: RichMessage, refTag: Int, reason: Int, text: String = ""): RichBusinessMessageReject = {
    val reject = RichBusinessMessageReject.newMessage

    reject.refMessageType = request.messageType
    reject.refMessageSequenceNumber = request.sequenceNumber
    reject.refTag = refTag
    reject.businessRejectReason = reason
    if (!text.isEmpty) {
      reject.text = text
    }

    reject
  }
}