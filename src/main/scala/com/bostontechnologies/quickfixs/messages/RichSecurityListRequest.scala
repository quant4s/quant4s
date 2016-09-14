package com.bostontechnologies.quickfixs.messages

import quickfix.Message
import quickfix.field.{SecurityListRequestType, SecurityReqID, MsgType}
import quickfix.fix50.SecurityListRequest

class RichSecurityListRequest(self: Message) extends RichMessage(self) {

	require(RichMessage.isA(self, RichSecurityListRequest.msgType))

	def hasRequestId: Boolean = self.isSetField(SecurityReqID.FIELD)

	def requestId: String = self.getString(SecurityReqID.FIELD)

	def requestId_=(requestId: String){
		self.setString(SecurityReqID.FIELD, requestId)
	}

	def hasRequestType: Boolean = self.isSetField(SecurityListRequestType.FIELD)

	def requestType: Int = self.getInt(SecurityListRequestType.FIELD)

	def requestType_=(requestType: Int){
		self.setInt(SecurityListRequestType.FIELD, requestType)
	}

}

object RichSecurityListRequest extends RichMessageExtractor[RichSecurityListRequest, SecurityListRequest] {

	val msgType = MsgType.SECURITY_LIST_REQUEST

	def apply(message: quickfix.fix50.SecurityListRequest): RichSecurityListRequest =
		new RichSecurityListRequest(message)

	def new50Message: RichSecurityListRequest = this(new quickfix.fix50.SecurityListRequest)

	def apply(message: quickfix.fix44.SecurityListRequest): RichSecurityListRequest =
		new RichSecurityListRequest(message)

	def new44Message: RichSecurityListRequest = this(new quickfix.fix44.SecurityListRequest)

}
