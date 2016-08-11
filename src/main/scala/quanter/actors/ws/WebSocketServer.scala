/**
  *
  */
package quanter.actors.ws

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, Props}
import quanter.actors.ws.SimpleServer.{Push, PushToChildren}
import spray.can.server.UHttp
import spray.can.{Http, websocket}
/*
  object WebSocketServer {
    def props() = Props(classOf[WebSocketServer])
  }

/**
  *
  */
class WebSocketServer extends Actor with ActorLogging{
  def receive = {
    // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
    case Http.Connected(remoteAddress, localAddress) =>
      // val serverConnection = sender()
      // val conn = context.actorOf(WebSocketWorker.props(serverConnection))
      // serverConnection ! Http.Register(conn)
    case websocket.HandshakeRequest(state) =>
      state match {
        case wsFailure: websocket.HandshakeFailure => sender() ! wsFailure.response
        case wsContext: websocket.HandshakeContext => sender() ! UHttp.UpgradeServer(websocket.pipelineStage(self, wsContext), wsContext.response)
      }

    // upgraded successfully
    case UHttp.Upgraded =>
//      context.become(businessLogic orElse closeLogic)
      self ! websocket.UpgradedToWebSocket // notify Upgraded to WebSocket protocol

  }
}
*/