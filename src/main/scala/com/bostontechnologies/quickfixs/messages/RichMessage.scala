package com.bostontechnologies.quickfixs.messages

import quickfix.Message
import quickfix.field._

class RichMessage private[messages](val self: Message) extends RichFieldMap[Message] {

	require(self != null)

	def messageType: String = RichMessage.messageType(self)

	def messageType_=(value: String){
		self.getHeader.setString(MsgType.FIELD, value)
	}

  def sequenceNumber: Int = self.getHeader.getInt(MsgSeqNum.FIELD)

  def sequenceNumber_=(value: Int) {
    self.getHeader.setInt(MsgSeqNum.FIELD, value)
  }

	def isA(fixType: String): Boolean = messageType == fixType

	def hasOnBehalfOf: Boolean = self.getHeader.isSetField(OnBehalfOfCompID.FIELD)

	def onBehalfOf: String = self.getHeader.getString(OnBehalfOfCompID.FIELD)

	def onBehalfOf_=(value: String) {
		self.getHeader.setString(OnBehalfOfCompID.FIELD, value)
	}

	def hasDeliverTo: Boolean = self.getHeader.isSetField(DeliverToCompID.FIELD)

	def deliverTo: String = self.getHeader.getString(DeliverToCompID.FIELD)

	def deliverTo_=(value: String) {
		self.getHeader.setString(DeliverToCompID.FIELD, value)
	}

  def senderCompId_=(value: String) {
    self.getHeader.setString(SenderCompID.FIELD, value)
  }

  def senderCompId: String =
    self.getHeader.getString(SenderCompID.FIELD)

  def senderSubId: String =
    self.getHeader.getString(SenderSubID.FIELD)

  def senderSubId_=(value: String) {
    self.getHeader.setString(SenderSubID.FIELD, value)
  }

  def targetCompId_=(value: String) {
    self.getHeader.setString(TargetCompID.FIELD, value)
  }

  def targetCompId: String =
    self.getHeader.getString(TargetCompID.FIELD)

  def beginString_=(value:String) {
   self.getHeader.setString(BeginString.FIELD, value)
  }

  def beginString: String =
    self.getHeader.getString(BeginString.FIELD)

  def removeHeaderField(tag: Int) {
    self.getHeader.removeField(tag)
  }

  def removeBodyField(tag: Int) {
    self.removeField(tag)
  }

  def removeTrailerField(tag: Int) {
    self.getTrailer.removeField(tag)
  }

  def stringField(tag: Int) = self.getString(tag)

  def charField(tag: Int) = self.getChar(tag)

  def intField(tag: Int) = self.getInt(tag)

  def decimalField(tag: Int) = self.getDecimal(tag)

  def has(tag: Int) = self.isSetField(tag)

	override def toString = self.toString
}

object RichMessage {

	def messageType(message: Message): String = message.getHeader.getString(MsgType.FIELD)

	def isA(message: Message, msgType: String): Boolean = messageType(message) == msgType

	def newMessage(msgType: String = ""): RichMessage = {
		val message = new RichMessage(new Message)
		if (!msgType.isEmpty) {
			message.messageType = msgType
		}
		message
	}

	def apply(message: Message): RichMessage = new RichMessage(message)

}

trait RichMessageExtractor[RichMessage, FixMessage <: Message] {

    val msgType: String

    def apply(message: FixMessage): RichMessage

    def unapply(message: quickfix.Message): Option[RichMessage] =
    message.getHeader.getString(MsgType.FIELD) == msgType match {
      case true => Some(apply(message.asInstanceOf [FixMessage]))
      case false => None
  }
}
