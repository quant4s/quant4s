package com.bostontechnologies.quickfixs.messages

import quickfix.Message
import quickfix.fixt11.Reject
import quickfix.field._

class RichSessionReject private(self: Message)
  extends RichMessage(self) {

  require(RichMessage.isA(self, RichSessionReject.msgType))

  def refMessageSequenceNumber: Int = self.getInt(RefSeqNum.FIELD)

  def refMessageSequenceNumber_=(value: Int) {
    self.setInt(RefSeqNum.FIELD, value)
  }

  def hasText: Boolean = self.isSetField(Text.FIELD)

  def text: String = self.getString(Text.FIELD)

  def text_=(value: String) {
    self.setString(Text.FIELD, value)
  }

  def hasRefMessageType: Boolean = self.isSetField(RefMsgType.FIELD)

  def refMessageType: String = self.getString(RefMsgType.FIELD)

  def refMessageType_=(value: String) {
    self.setString(RefMsgType.FIELD, value)
  }

  def hasSessionRejectReason: Boolean = self.isSetField(SessionRejectReason.FIELD)

  def sessionRejectReason: Int = self.getInt(SessionRejectReason.FIELD)

  def sessionRejectReason_=(value: Int) {
    self.setInt(SessionRejectReason.FIELD, value)
  }

  def hasRefTag: Boolean = self.isSetField(RefTagID.FIELD)

  def refTag: Int = self.getInt(RefTagID.FIELD)

  def refTag_=(value: Int) {
    self.setInt(RefTagID.FIELD, value)
  }
}

object RichSessionReject extends RichMessageExtractor[RichSessionReject, Reject] {

  val msgType = MsgType.REJECT

  def apply(message: quickfix.fixt11.Reject): RichSessionReject =
    new RichSessionReject(message)

  def new50Message: RichSessionReject = this(new quickfix.fixt11.Reject())

  def apply(message: quickfix.fix44.Reject): RichSessionReject =
    new RichSessionReject(message)

  def new44Message: RichSessionReject = this(new quickfix.fixt11.Reject())

  def newMessage: RichSessionReject = new RichSessionReject(RichMessage.newMessage(msgType).self)

  def apply(request: RichMessage, refTag: Int, reason: Int, text: String = ""): RichSessionReject = {
    val reject = RichSessionReject.newMessage

    reject.refMessageType = request.messageType
    reject.refMessageSequenceNumber = request.sequenceNumber
    reject.refTag = refTag
    reject.sessionRejectReason = reason
    if (!text.isEmpty) {
      reject.text = text
    }

    reject
  }
}