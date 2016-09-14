package com.bostontechnologies.quickfixs.sender

import quickfix.{Session, Message, SessionID}

/**
 * A basic implementation of the FixSender trait. The object itself is totally stateless and thus thread-safe but
 * still use QuickFix Session methods to identify existing sessions.
 */
class StaticFixSender extends FixSender {

  def send(message: Message, sessionId: SessionID): Boolean =
	  Session.lookupSession(sessionId) match {
			case null => false
	    case session => session.send(message)
    }

}
