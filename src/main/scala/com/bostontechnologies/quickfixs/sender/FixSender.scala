package com.bostontechnologies.quickfixs.sender

import quickfix.{Message, SessionID}
import com.bostontechnologies.quickfixs.messages.RichMessage

/**
 * This trait defines a set of methods which can be used to send Fix messages.
 */
trait FixSender {

  def send(message: Message, sessionId: SessionID): Boolean

  def send(message: RichMessage, sessionId: SessionID): Boolean =
	  send(message.self, sessionId)

}
