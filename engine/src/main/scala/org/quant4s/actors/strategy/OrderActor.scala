/**
  *
  */
package org.quant4s.actors.strategy

import akka.actor.{ActorLogging, FSM, Props}
import org.quant4s.actors.strategy.OrderActor._

/**
  *
  */
class OrderActor(id: Int) extends FSM[OrderState, OrderData] with ActorLogging{

  when(New) {
    // TODO: 接受撤销单
    case _ => stay()
  }
  when(Sent) {
    // TODO: 接受 回报， 接受撤销单
    // 1. 接收到成功回报， 订单over 接收到拒绝回报， 订单reject
    // 3. 接收到撤销请求， 订单canceled
    case Event(_, _) => goto(Over)
//    case Event(_, _) => goto(Canceled)
  }
  when(Canceled) {
    // TODO: 接收撤销成功或者失败的回报，是否已经撤销
    //  1. 接收到成功回报， 订单over 接收到拒绝回报， 订单reject, 接收到撤销回报
    // 2. 接收到撤销成功的
    case Event(_, _) => goto(Rejected)
//    case _ => stay()
  }
  when (Rejected) {
    case _ => stay()
  }
  when(Over) {  // 订单结束
    case _ => stay()
  }
  when (Expired) {
    // 假如这个订单保留到了结算以后， 就是过期单。发出撤销指令
    case _ => stay()
  }
}

object OrderActor {
  def props(id: Int) = Props(classOf[OrderActor], id)

  sealed trait OrderState

  case object New extends OrderState
  case object Sent extends OrderState
  case object Rejected extends OrderState
  case object Canceled extends OrderState
  case object Expired extends OrderState
  case object Over extends OrderState

  case class OrderData()
}


