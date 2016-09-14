package com.bostontechnologies.quickfixs.authentication

import quickfix.{Message, SessionID, Application}
import quickfix.field.{Password, Username, MsgType}
import com.bostontechnologies.quickfixs.messages.RichMessage

/**
 * This trait provides an implementation of the toAdmin method from the Application interface. It intercepts
 * logon messages and add the appropriate fix credentials to the message.
 */
trait AuthenticatingInitiatorApplication extends Application {

	val fixCredentialsProvider: FixCredentialsProvider

  abstract override def toAdmin(message: Message, session: SessionID) {
    if (RichMessage.isA(message, MsgType.LOGON)) {
	    fixCredentialsProvider.get(session) match {
		    case Some(credentials) => {
			    message.setField(new Username(credentials.login))
			    message.setField(new Password(credentials.password))
		    }
		    case None =>
	    }
    }

    super.toAdmin(message, session)
  }
}
