/**
  *
  */
package org.quant4s.rest

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString

/**
  *
  */
class FlashServer  extends Actor  with ActorLogging {
  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("localhost", 843))

  def receive = {
    case b @ Bound(localAddress) =>
    // do some logging or setup ...

    case CommandFailed(_: Bind) => context stop self

    case c @ Connected(remote, local) =>
      val handler = context.actorOf(Props[SimplisticHandler])
      val connection = sender()
      connection ! Register(handler)
  }

//  def actorRefFactory = context
//
//  def systemRef = context.system
//
//  implicit def executionContext = actorRefFactory.dispatcher
//
//  private val allowOriginHeader = `Access-Control-Allow-Origin`(AllOrigins)
//  private val optionsCorsHeaders = List(
//    `Access-Control-Allow-Headers`("Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent, Authorization"),
//    `Access-Control-Max-Age`(1728000)) //20 days
//
//  def receive = runRoute(respondWithHeaders(`Access-Control-Allow-Methods`(OPTIONS, GET, POST, DELETE, PUT) :: allowOriginHeader :: optionsCorsHeaders) {
//    policyServiceRoute
//  })
//
//  def policyServiceRoute(implicit log: LoggingContext) = {
//    get {
//      path("<policy-file-request/>") {
//        // 获取策略列表
//        complete {
//          log.info("请求安全策略文件")
//          """
//            |<?xml version=\"1.0\"?>
//            |<cross-domain-policy>
//            | <site-control permitted-cross-domain-policies="all"/>
//            | <allow-access-from domain="*" to-ports="*" />
//            |</cross-domain-policy>
//          """.stripMargin
//        }
//      }
//    }
//  }
}

class SimplisticHandler extends Actor with ActorLogging{
  import Tcp._

  val policy =           """
                           |<cross-domain-policy>
                           |  <allow-access-from domain="localhost" to-ports="100-64000"/>
                           |</cross-domain-policy>
                         """.stripMargin
  def receive = {
    case Received(data) =>
      log.debug(data.decodeString("utf-8"))
      sender ! Write(ByteString(policy))
      log.debug(policy)
    case PeerClosed     => context stop self
  }
}

object FlashServer {
  def props: Props = {
    Props(classOf[FlashServer])
  }

  val path = "flashServer"
}
