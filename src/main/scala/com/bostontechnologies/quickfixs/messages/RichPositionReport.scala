package com.bostontechnologies.quickfixs.messages

import quickfix.{Group, Message}
import quickfix.field._
import scala.collection.JavaConversions._
import com.bostontechnologies.quickfixs.components.PositionQuantity
import com.bostontechnologies.quickfixs.fields.RichParties
import quickfix.fix50.PositionReport

class RichPositionReport private(self: Message) extends RichMessage(self) with InstrumentFields[Message] with RichParties {

  require(RichMessage.isA(self, RichPositionReport.msgType))

  def hasPositionReportId: Boolean = self.isSetField(PosMaintRptID.FIELD)

  def positionReportId: String = self.getString(PosMaintRptID.FIELD)

  def positionReportId_=(id: String) {
    self.setString(PosMaintRptID.FIELD, id)
  }

  def hasPositionRequestId: Boolean = self.isSetField(PosReqID.FIELD)

  def positionRequestId: String = self.getString(PosReqID.FIELD)

  def positionRequestId_=(id: String) {
    self.setString(PosReqID.FIELD, id)
  }

  def hasPositionRequestResult: Boolean = self.isSetField(PosReqResult.FIELD)

  def positionRequestResult: Int = self.getInt(PosReqResult.FIELD)

  def positionRequestResult_=(result: Int) {
    self.setInt(PosReqResult.FIELD, result)
  }

  def positionQuantityCount: Int = self.getInt(NoPositions.FIELD)

  def positionQuantities: Seq[PositionQuantity] = self.getGroups(NoPositions.FIELD).map(PositionQuantity(_))

  def +=(positionQuantity: PositionQuantity) {
    val group = new Group(NoPositions.FIELD, PosType.FIELD)
    group.setFields(positionQuantity.toFields)
    self.addGroup(group)
  }

  def addPositionQuantities(positionQuantities: Iterable[PositionQuantity]) {
    positionQuantities.foreach(this += _)
  }
}

object RichPositionReport extends RichMessageExtractor[RichPositionReport, PositionReport] {

  val msgType = MsgType.POSITION_REPORT

  def apply(message: quickfix.fix50.PositionReport): RichPositionReport = new RichPositionReport(message)

  def new50Message: RichPositionReport = this(new quickfix.fix50.PositionReport)

  def apply(message: quickfix.fix44.PositionReport): RichPositionReport = new RichPositionReport(message)

  def new44Message: RichPositionReport = this(new quickfix.fix44.PositionReport)

  def newMessage: RichPositionReport = new RichPositionReport(RichMessage.newMessage(msgType).self)
}