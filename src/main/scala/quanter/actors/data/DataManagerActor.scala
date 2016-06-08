package quanter.actors.data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.collection.mutable

case class CreateIndicatorActor(json: String)
case class CreateBarActor(json: String)
case class CreateTickActor(json: String)

class DataManagerActor extends Actor with ActorLogging {

  var barRefs = new mutable.HashMap[String, ActorRef]()
  var tickRefs = new mutable.HashMap[String, ActorRef]()
  var indicatorRefs = new mutable.HashMap[String, ActorRef]()

  override def receive: Receive = {
    case CreateBarActor(json) => _createBarActor(json)
    case CreateTickActor(json) => _createTickActor(json)
    case CreateIndicatorActor(json) => _createIndicatorActor(json)
    case _ =>
  }

  private def _createBarActor(json: String): Unit = {
    if(!barRefs.contains(json)) {
      val ref = context.actorOf(BarActor.props(json))
      barRefs += (json -> ref)
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
      val ref = context.actorOf(IndicatorActor.props(json))
      barRefs += (json -> ref)
    }
  }

}

object DataManagerActor {
  val PATH = "dataManager"

  def props =  {
    Props.create(classOf[DataManagerActor])
  }
}
