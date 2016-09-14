package com.msilb.scalanda.streamapi

import akka.actor.{ActorRef, FSM, Props}
import akka.io.IO
import com.msilb.scalanda.common.Environment
import com.msilb.scalanda.common.Environment.SandBox
import com.msilb.scalanda.common.model.Transaction
import com.msilb.scalanda.common.model.TransactionJsonProtocol._
import com.msilb.scalanda.streamapi.StreamingConnector._
import com.msilb.scalanda.streamapi.model.HeartbeatJsonProtocol._
import com.msilb.scalanda.streamapi.model.TickJsonProtocol._
import com.msilb.scalanda.streamapi.model.{Heartbeat, Tick}
import spray.can.Http
import spray.can.Http.HostConnectorInfo
import spray.http.Uri.Query
import spray.http._
import spray.httpx.RequestBuilding._
import spray.json._

import scala.concurrent.duration._

object StreamingConnector {

  def props = Props(classOf[StreamingConnector])

  case class Connect(env: Environment = SandBox, authToken: Option[String] = None)

  case class StartRatesStreaming(accountId: Int, instruments: Set[String], sessionId: Option[String] = None)

  case class StartEventsStreaming(accountIds: Option[Set[Int]] = None)

  case class AddListeners(listeners: Set[ActorRef])

  case class RemoveListeners(listeners: Set[ActorRef])


  case object ConnectionEstablished

  sealed trait State

  case object Disconnected extends State

  case object Connecting extends State

  case object Connected extends State

  sealed trait Data

  case object Empty extends Data

  case class CurrentData(requesterInfo: ActorRef, hostConnector: ActorRef, listeners: Set[ActorRef]) extends Data

}

class StreamingConnector extends FSM[State, Data] {

  import context.system

  startWith(Disconnected, Empty)

  when(Disconnected) {
    case Event(Connect(env, authTokenOpt), _) =>
      IO(Http) ! Http.HostConnectorSetup(
        host = env.streamApiUrl(),
        port = if (env.authenticationRequired()) 443 else 80,
        sslEncryption = env.authenticationRequired(),
        defaultHeaders = authTokenOpt.map(authToken => List(HttpHeaders.Authorization(OAuth2BearerToken(authToken)))).getOrElse(Nil)
      )
      goto(Connecting) forMax 5.seconds using CurrentData(sender(), system.deadLetters, Set.empty)
  }

  when(Connecting) {
    case Event(HostConnectorInfo(hostConnector, _), data: CurrentData) =>
      goto(Connected) using data.copy(hostConnector = hostConnector)
  }

  onTransition {
    case Connecting -> Connected =>
      (stateData: @unchecked) match {
        case CurrentData(requester, _, _) => requester ! ConnectionEstablished
      }
  }

  when(Connected) {
    case Event(StartRatesStreaming(accountId, instruments, sessionIdOpt), CurrentData(_, connector, _)) =>
      val uri = Uri("/v1/prices").withQuery(
        Query.asBodyData(
          Seq(
            Some(("accountId", accountId.toString)),
            Some(("instruments", instruments.mkString(","))),
            sessionIdOpt.map(s => ("sessionId", s))
          ).flatten
        )
      )
      connector ! Get(uri)
      stay()
    case Event(StartEventsStreaming(accountIdsOpt), CurrentData(_, connector, _)) =>
      val uri = Uri("/v1/events").withQuery(
        Query.asBodyData(
          Seq(
            accountIdsOpt.map(accountIds => ("accountIds", accountIds.mkString(",")))
          ).flatten
        )
      )
      connector ! Get(uri)
      stay()
    case Event(e: ChunkedResponseStart, _) =>
      log.debug("Received ChunkedResponseStart: {}", e)
      stay()
    case Event(MessageChunk(data, _), CurrentData(_, _, refs)) =>
      data.asString.lines.foreach { line =>
        line.parseJson.asJsObject.fields.head match {
          case ("transaction", obj) =>
            val t = obj.convertTo[Transaction]
            refs.foreach(_ ! t)
            log.info("Received new transaction: {}", t)
          case ("tick", obj) =>
            val t = obj.convertTo[Tick]
            refs.foreach(_ ! t)
            log.info("Received new price tick: {}", t)
          case ("heartbeat", obj) =>
            val h = obj.convertTo[Heartbeat]
            log.debug("Received heartbeat: {}", h)
          case (unknown, _) =>
            log.warning("Unknown event received: {}", unknown)
        }
      }
      stay()
    case Event(e: ChunkedMessageEnd, _) =>
      log.debug("Received ChunkedMessageEnd: {}", e)
      stay()
  }

  whenUnhandled {
    case Event(AddListeners(refs), currentData: CurrentData) =>
      val newListeners = currentData.listeners ++ refs
      stay() using currentData.copy(listeners = newListeners) replying newListeners
    case Event(RemoveListeners(refs), currentData: CurrentData) =>
      val newListeners = currentData.listeners -- refs
      stay() using currentData.copy(listeners = newListeners) replying newListeners
  }
}
