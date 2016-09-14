package com.bostontechnologies.quickfixs.messages

import quickfix.Message
import quickfix.field._
import quickfix.fix50.UserRequest

class RichUserRequest(self: Message) extends RichMessage(self) {

	require(RichMessage.isA(self, RichUserRequest.msgType))

	def hasRequestId: Boolean = self.isSetField(UserRequestID.FIELD)

	def requestId: String = self.getString(UserRequestID.FIELD)

	def requestId_=(requestId: String){
		self.setString(UserRequestID.FIELD, requestId)
	}

	def hasUsername: Boolean = self.isSetField(Username.FIELD)

	def username: String = self.getString(Username.FIELD)

	def username_=(value: String){
		self.setString(Username.FIELD, value)
	}

	def hasPassword: Boolean = self.isSetField(Password.FIELD)

	def password: String = self.getString(Password.FIELD)

	def password_=(value: String){
		self.setString(Password.FIELD, value)
	}

	def hasCredentials: Boolean = hasUsername && hasPassword

	def credentials: (String, String) = (username, password)

	def credentials_=(usernamePassword: (String, String)){
		username = usernamePassword._1
		password = usernamePassword._2
	}

	def hasRequestType: Boolean = self.isSetField(UserRequestType.FIELD)

	def requestType: Int = self.getInt(UserRequestType.FIELD)

	def requestType_=(requestType: Int){
		self.setInt(UserRequestType.FIELD, requestType)
	}

}

object RichUserRequest extends RichMessageExtractor[RichUserRequest, UserRequest] {

	val msgType = MsgType.USER_REQUEST

	def apply(message: quickfix.fix50.UserRequest): RichUserRequest =
		new RichUserRequest(message)

	def new50Message: RichUserRequest = this(new quickfix.fix50.UserRequest)

	def apply(message: quickfix.fix44.UserRequest): RichUserRequest =
		new RichUserRequest(message)

	def new44Message: RichUserRequest = this(new quickfix.fix44.UserRequest)

}
