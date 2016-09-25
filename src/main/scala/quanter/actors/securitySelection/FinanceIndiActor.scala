/**
  *
  */
package quanter.actors.securitySelection

import akka.actor.{Actor, ActorLogging}
import quanter.securitySelection.{Instrument, Selector}

/**
  *
  */
class FinanceIndiActor extends  Actor with ActorLogging{
  val list: List[Instrument] = null

  override def receive: Receive = {
    case pe: PE => _handlePE(pe.op, pe.value)
    case pb: PB => _handlePB(pb.op, pb.value)
    case roe: ROE => _handlePB(roe.op, roe.value)
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

