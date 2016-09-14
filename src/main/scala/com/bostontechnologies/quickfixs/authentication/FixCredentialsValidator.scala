package com.bostontechnologies.quickfixs.authentication

import quickfix.SessionID

trait FixCredentialsValidator {

	def isValid(credentials: Credentials, sessionId: SessionID): Boolean

}
