/**
  *
  */
package quanter.trade.quickfixs.authentication

import quanter.trade.quickfixs.SessionID


trait FixCredentialsValidator {

  def isValid(credentials: Credentials, sessionId: SessionID): Boolean

}
