package quanter.actors.trade

import com.sun.jna.Callback

/**
  *
  */
trait CallBack extends Callback{
  def messageReceived(paramString: String)
}
