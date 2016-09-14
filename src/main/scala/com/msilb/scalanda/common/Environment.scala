package com.msilb.scalanda.common

sealed trait Environment {
  def restApiUrl(): String

  def streamApiUrl(): String

  def authenticationRequired(): Boolean
}

object Environment {

  case object SandBox extends Environment {
    val restApiUrl = "api-sandbox.oanda.com"
    val streamApiUrl = "stream-sandbox.oanda.com"
    val authenticationRequired = false
  }

  case object Practice extends Environment {
    val restApiUrl = "api-fxpractice.oanda.com"
    val streamApiUrl = "stream-fxpractice.oanda.com"
    val authenticationRequired = true
  }

  case object Production extends Environment {
    val restApiUrl = "api-fxtrade.oanda.com"
    val streamApiUrl = "stream-fxtrade.oanda.com"
    val authenticationRequired = true
  }

}
