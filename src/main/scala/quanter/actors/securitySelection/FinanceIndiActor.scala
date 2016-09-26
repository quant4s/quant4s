/**
  *
  */
package quanter.actors.securitySelection

import akka.actor.{Actor, ActorLogging}
import quanter.rest.FinanceIndi
import quanter.securitySelection.{Instrument, Selector}

/**
  *
  */
class FinanceIndiActor extends  Actor with ActorLogging{
  val list: List[Instrument] = null

  override def receive: Receive = {
    case indi: FinanceIndi => _handleIndi(indi)
  }

  def _handleIndi(cmd: FinanceIndi): Unit = {
    cmd.name match {
      case "PE" => _handlePE(cmd.op, cmd.value)
      case "PB" => _handlePB(cmd.op, cmd.value)
      case "ROE" => _handleROE(cmd.op, cmd.value)
    }
  }

  def _handlePE(op: String, value: Double): Unit =  sender ! new Selector(list).filter(_cmp(op, ins=> ins.pe, value))

  def _handlePB(op: String, value: Double): Unit =  sender ! new Selector(list).filter(_cmp(op, ins=> ins.pb, value))

  def _handleROE(op: String, value: Double): Unit = sender ! new Selector(list).filter(_cmp(op, ins=> ins.pb, value))

  def _cmp(op: String , field: Instrument => Double, value: Double): Instrument => Boolean = {
    op match {
      case "ge" => ins: Instrument => {field.apply(ins) >= value}
      case "le" => ins: Instrument => {field.apply(ins) >= value}
    }
  }
}

