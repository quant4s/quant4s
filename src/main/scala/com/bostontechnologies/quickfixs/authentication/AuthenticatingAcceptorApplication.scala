package com.bostontechnologies.quickfixs.authentication

import quickfix.field.{Password, Username, MsgType}
import quickfix.{RejectLogon, Message, SessionID, Application}
import com.bostontechnologies.quickfixs.messages.RichMessage

/**
 * This trait overrides the fromAdmin method from the Application interface to check the logon message
 * sent by the Initiator to make sure the provided credentials are correct.
 */
trait AuthenticatingAcceptorApplication extends Application {

  val fixCredentialsValidator: FixCredentialsValidator

	abstract override def fromAdmin(message: Message, sessionId: SessionID) {
    if (RichMessage.isA(message, MsgType.LOGON)){
			val username = message.getField(new Username()).getValue
			val password = message.getField(new Password()).getValue
      val fixCredentials = new Credentials(username, password)

	    fixCredentialsValidator.isValid(fixCredentials, sessionId) match {
		    case true => super.fromAdmin(message, sessionId)
		    case false => throw new RejectLogon("Invalid credentials")
	    }
		}
	}

}
