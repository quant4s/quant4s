package com.bostontechnologies.quickfixs.messages

import quickfix.Message
import quickfix.field.{MsgType, PosReqID, PosReqType}
import com.bostontechnologies.quickfixs.fields.RichParties
import quickfix.fix50.RequestForPositions

class RichPositionRequest private(self: Message) extends RichMessage(self) with RichParties {

  require(RichMessage isA (self, MsgType.REQUEST_FOR_POSITIONS))

  def hasRequestId: Boolean = self.isSetField(PosReqID.FIELD)

  def requestId: String = self.getString(PosReqID.FIELD)

  def requestId_=(id: String) {
    self.setString(PosReqID.FIELD, id)
  }

  def hasRequestType: Boolean = self.isSetField(PosReqType.FIELD)

  def requestType: Int = self.getInt(PosReqType.FIELD)

  def requestType_=(value: Int) {
    self.setInt(PosReqType.FIELD, value)
  }
}

object RichPositionRequest extends RichMessageExtractor[RichPositionRequest, RequestForPositions] {

  val msgType = MsgType.REQUEST_FOR_POSITIONS

  def apply(message: quickfix.fix50.RequestForPositions): RichPositionRequest = new RichPositionRequest(message)

  def new50Message: RichPositionRequest = this(new quickfix.fix50.RequestForPositions)

  def apply(message: quickfix.fix44.RequestForPositions): RichPositionRequest = new RichPositionRequest(message)

  def new44Message: RichPositionRequest = this(new quickfix.fix44.RequestForPositions)
}