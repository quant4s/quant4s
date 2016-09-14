package com.bostontechnologies.quickfixs.authentication

import quickfix.SessionID

trait FixCredentialsProvider {

	def get(sessionId: SessionID): Option[Credentials]
}

case class Credentials(login:String, password:String)