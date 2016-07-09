package quanter.actors.data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.collection.mutable

case class RequestIndicatorData(sub: String)
case class RequestBarData(sub: String)
case class RequestTickData(sub: String)

class DataManagerActor extends Actor with ActorLogging {

  var barRefs = new mutable.HashMap[String, ActorRef]()
  var tickRefs = new mutable.HashMap[String, ActorRef]()
  var indicatorRefs = new mutable.HashMap[String, ActorRef]()

  override def receive: Receive = {
    case RequestBarData(json) => _createBarActor(json)
    case RequestTickData(json) => _createTickActor(json)
    case RequestIndicatorData(json) => _createIndicatorActor(json)
    case _ =>
  }

  private def _createBarActor(sub: String): Unit = {
    if(!barRefs.contains(sub)) {
      val ref = context.actorOf(BarActor.props(sub))
      barRefs += (sub -> ref)
    }
  }

  private def _createTickActor(json: String): Unit = {
    if(!barRefs.contains(json)) {
      val ref = context.actorOf(TickActor.props(json))
      barRefs += (json -> ref)
    }
  }

  private def _createIndicatorActor(json: String): Unit = {
    if(!barRefs.contains(json)) {
      val ref = context.actorOf(IndicatorActor.props(json), json)
      barRefs += (json -> ref)

      println("create indicator actor")
    }
  }

}

object DataManagerActor {
  val path = "dataManager"

  def props =  {
    Props.create(classOf[DataManagerActor])
  }
}
