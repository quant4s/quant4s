package com.bostontechnologies.quickfixs.messages

import quickfix.Message
import quickfix.fix50.{CollateralReport, CollateralInquiry, CollateralInquiryAck}
import com.bostontechnologies.quickfixs.fields._
import quickfix.field.{MsgType, CollStatus => QFCollStatus, CollRptID, CollInquiryStatus}

class RichCollateralInquiry private(self: Message)
  extends RichMessage(self)
  with RichSide
  with RichAccount
  with RichCollateralInquiryID {

  require(RichMessage.isA(self, RichCollateralInquiry.msgType))
}

object RichCollateralInquiry {

  val msgType = MsgType.COLLATERAL_INQUIRY

  def apply(message: quickfix.fix50.CollateralInquiry): RichCollateralInquiry =
    new RichCollateralInquiry(message)

  def newMessage = new RichCollateralInquiry(RichMessage.newMessage(msgType).self)

  def newFix50Message = new RichCollateralInquiry(new CollateralInquiry)

  def newFix44Message = new RichCollateralInquiry(new quickfix.fix44.CollateralInquiry)
}

object CollateralInquiryStatus extends Enumeration {
  type CollateralInquiryStatus = Value
  val Accepted = Value(0)
  val AcceptedWithWarnings = Value(1)
  val Completed = Value(2)
  val CompletedWithWarnings = Value(3)
  val Rejected = Value(4)
}

class RichCollateralInquiryAck private(self: Message)
  extends RichMessage(self)
  with RichAccount
  with RichCollateralInquiryID {

  require(RichMessage.isA(self, RichCollateralInquiryAck.msgType))

  def collateralInquiryStatus = self.getInt(CollInquiryStatus.FIELD)
}

object RichCollateralInquiryAck {

  import CollateralInquiryStatus._

  val msgType = MsgType.COLLATERAL_INQUIRY_ACK

  def newMessage = new RichCollateralInquiryAck(RichMessage.newMessage(msgType).self)

  def newFix50Message = new RichCollateralInquiryAck(new CollateralInquiryAck)

  def newFix50Message(collInquiryStatus: CollateralInquiryStatus) = {
    val collateralInquiryAck = new CollateralInquiryAck
    collateralInquiryAck.setInt(CollInquiryStatus.FIELD, collInquiryStatus.id)
    new RichCollateralInquiryAck(collateralInquiryAck)
  }
}

object CollStatus extends Enumeration {
  type CollStatus = Value
  val Unassigned = Value(0)
  val PartiallyAssigned = Value(1)
  val AssignmentProposed = Value(2)
  val Assigned = Value(3)
  val Challenged = Value(4)
}

class RichCollateralReport private(self: Message)
  extends RichMessage(self)
  with RichAccount
  with RichTotalNetValue
  with RichCashOutstanding {

  require(RichMessage.isA(self, RichCollateralReport.msgType))

  def collRptID = self.getString(CollRptID.FIELD)

  def collRptID_=(value: String) {
    self.setString(CollRptID.FIELD, value)
  }

  def collStatus = self.getInt(QFCollStatus.FIELD)

  def collStatus_=(value: Int) {
    self.setInt(QFCollStatus.FIELD, value)
  }
}

object RichCollateralReport {

  import CollStatus._

  val msgType = MsgType.COLLATERAL_REPORT

  def newMessage = new RichCollateralReport(RichMessage.newMessage(msgType).self)

  def new50Message = new RichCollateralReport(new CollateralReport())

  def apply(reportId: String, status: CollStatus) = {
    val report = RichCollateralReport.newMessage
    report.collRptID = reportId
    report.collStatus = status.id

    report
  }
}

