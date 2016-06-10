/**
  *
  */
package quanter.trade.quickfixs.authentication

import quanter.trade.quickfixs.SessionID

case class Credentials(login:String, password:String)

trait FixCredentialsProvider {
  def get(sessionId: SessionID): Option[Credentials]
}
