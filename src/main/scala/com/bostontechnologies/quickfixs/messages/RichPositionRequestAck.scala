package com.bostontechnologies.quickfixs.messages

import quickfix.Message
import com.bostontechnologies.quickfixs.fields.RichParties
import quickfix.field._
import quickfix.fix50.RequestForPositionsAck

class RichPositionRequestAck private(self: Message)
  extends RichMessage(self)
  with RichParties {

  require(RichMessage.isA(self, MsgType.REQUEST_FOR_POSITIONS_ACK))

  def hasRequestId: Boolean = self.isSetField(PosReqID.FIELD)

  def requestId: String = self.getString(PosReqID.FIELD)

  def requestId_=(id: String) {
    self.setString(PosReqID.FIELD, id)
  }

  def hasRequestType: Boolean = self.isSetField(PosReqType.FIELD)

  def requestType: Int = self.getInt(PosReqType.FIELD)

  def requestType_=(id: String) {
    self.setString(PosReqType.FIELD, id)
  }

  def positionReportId: String = self.getString(PosMaintRptID.FIELD)

  def positionReportId_=(value: String) {
    self.setString(PosMaintRptID.FIELD, value)
  }

  def positionRequestStatus: Int = self.getInt(PosReqStatus.FIELD)

  def positionRequestStatus_=(value: Int) {
    self.setInt(PosReqStatus.FIELD, value)
  }

  def positionRequestResult: Int = self.getInt(PosReqResult.FIELD)

  def positionRequestResult_=(value: Int) {
    self.setInt(PosReqResult.FIELD, value)
  }
}

object RichPositionRequestAck extends RichMessageExtractor[RichPositionRequestAck, RequestForPositionsAck] {

  val msgType = MsgType.REQUEST_FOR_POSITIONS_ACK

  def apply(message: quickfix.fix50.RequestForPositionsAck): RichPositionRequestAck = new RichPositionRequestAck(message)

  def new50Message: RichPositionRequestAck = this(new quickfix.fix50.RequestForPositionsAck)

  def apply(message: quickfix.fix44.RequestForPositionsAck): RichPositionRequestAck =
     new RichPositionRequestAck(message)

  def new44Message: RichPositionRequestAck = this(new quickfix.fix44.RequestForPositionsAck)

  def newMessage: RichPositionRequestAck = new RichPositionRequestAck(RichMessage.newMessage(msgType).self)

  def apply(reportId: String, requestStatus: Int, requestResult: Int): RichPositionRequestAck = {
    val ack = RichPositionRequestAck.newMessage
    ack.positionReportId = reportId
    ack.positionRequestStatus = requestStatus
    ack.positionRequestResult = requestResult

    ack
  }
}