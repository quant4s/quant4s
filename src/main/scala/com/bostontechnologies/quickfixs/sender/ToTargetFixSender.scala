package com.bostontechnologies.quickfixs.sender

import quickfix.{SessionID, Message}
import com.bostontechnologies.quickfixs.messages.RichMessage

/**
 * This trait can be added to a class implementing FixSender if the senderCompId and the FIX version used to send fix messages
 * are always the same.
 */
trait ToTargetFixSender extends FixSender {

	val fixVersion: String
  val senderCompId: String

  def send(message: Message, targetCompId: String): Boolean =
    send(message, new SessionID(fixVersion, senderCompId, targetCompId))

  def send(message: RichMessage, targetCompId: String): Boolean =
    send(message.self, targetCompId)

}
