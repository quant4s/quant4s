package quanter.actors.data

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import quanter.actors.securities.SubscriptionSymbol

import scala.collection.mutable

case class RequestIndicatorData(topic: String, sub: String)
case class RequestBarData(topic: String, sub: String)
case class RequestTickData(topic: String, sub: String)
case class RequestFinanceData(topic: String, sub: String)

case class  Subscribe(sub: String)

/**
  * 订阅数据
  */
class DataManagerActor extends Actor with ActorLogging {

  var barRefs = new mutable.HashMap[String, ActorRef]()
  var tickRefs = new mutable.HashMap[String, ActorRef]()
  var indicatorRefs = new mutable.HashMap[String, ActorRef]()
  var financeRefs = new mutable.HashMap[String, ActorRef]()

  override def receive: Receive = {
    case req: RequestBarData => _createBarActor(req.topic, req.sub)
    case req: RequestTickData => _createTickActor(req.topic, req.sub)
    case req: RequestIndicatorData => _createIndicatorActor(req.topic, req.sub)
    case req: RequestFinanceData => _createFinanceActor(req.topic, req.sub)

    case sub: Subscribe => {
      val arr = sub.sub.split(",")
      val symbol = arr(0)
      val duration = arr(2).toInt
      val indiName = arr(1)
      val param = arr(3)
      val ref = context.actorOf(IndicatorActor.props(symbol, duration, indiName, param), sub.sub)
      indicatorRefs += (sub.sub -> ref)
      // _subscribe(sub.sub)
    }
    case _ =>
  }

  private def _createBarActor(topic: String, sub: String): Unit = {
    if(!barRefs.contains(sub)) {
      val ref = context.actorOf(BarActor.props(sub), topic)
      barRefs += (sub -> ref)
    }
  }

  private def _createTickActor(topic: String, subscription: String): Unit = {
    if(!tickRefs.contains(subscription)) {
      val ref = context.actorOf(TickActor.props(subscription), topic)
      tickRefs += (subscription -> ref)
    }
  }

  /**
    * 根据JSON来创建Indicator Actor, 每个indicator 可以有多个topic
    *
    * @param subscription
    * @param topic
    */
  private def _createIndicatorActor(topic: String, subscription: String): Unit = {
    //  如果是一个股票池
    if(_isPool(subscription)) {
      // TODO: 分析
    } else {
      if (!indicatorRefs.contains(subscription)) {

        // 如果是单个股票
        val arr = subscription.split(",")
        val symbol = arr(0)
        val duration = arr(2).toInt
        val indiName = arr(1)
        val param = arr(3)
        val ref = context.actorOf(IndicatorActor.props(symbol, duration, indiName, param, topic), topic)
        indicatorRefs += (subscription -> ref)

        log.info("创建指标%s".format(subscription))
      }
    }
  }

  private def _createFinanceActor(topic: String, sub: String): Unit = {
    if(!financeRefs.contains(sub)) {
      val ref = context.actorOf(BarActor.props(sub), topic)
      financeRefs += (sub -> ref)
    }
  }

  private def _isPool(value: String): Boolean = {
    false
  }
}

object DataManagerActor {
  val path = "dataManager"

  def props =  {
    Props.create(classOf[DataManagerActor])
  }
}
