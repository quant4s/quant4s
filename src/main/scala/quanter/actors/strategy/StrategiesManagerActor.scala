/**
  *
  */
package quanter.actors.strategy

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import quanter.actors._
import quanter.actors.persistence.PersistenceActor
import quanter.rest.{CancelOrder, HttpServer, Strategy, TradeAccount}
import quanter.strategies.StrategyCache

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._
case class UpdatePortfolio(strategy: Strategy)


/**
  * 1、策略的CRUD 操作
  * 2、买入|卖出股票
  * 3、
  */
class StrategiesManagerActor extends Actor{
  val persisRef = context.actorSelection("/user/" + PersistenceActor.path)
  val restRef = context.actorSelection("/user/" + HttpServer.path)

  var strategyRefs = new mutable.HashMap[Int, ActorRef]()

  override def receive: Receive = {
    // 策略相关操作
    case s: NewStrategy => _createStrategy(s.strategy)
    case s: DeleteStrategy => persisRef ! new DeleteStrategy(s.id)
    case s: ListStrategies => persisRef ! new ListStrategies()

    // 持久化层返回的一步消息，通知WEB层，构建缓存
    case s: Array[Strategy] => _createStrategyActors(s)
    case s: Strategy => restRef ! s
  }

  private def _createStrategy(strategy: Strategy) = {
    if(!strategyRefs.contains(strategy.id)) {
      val ref = context.actorOf(StrategyActor.props(strategy.id), strategy.id.toString)
      strategyRefs += (strategy.id -> ref)
      persisRef ! new NewStrategy(strategy)
    }
  }

  private def _createStrategyActors(strategies: Array[Strategy]): Unit = {
    for(s <- strategies) {
      if(!strategyRefs.contains(s.id)) {
        val ref = context.actorOf(StrategyActor.props(s.id), s.id.toString)
        strategyRefs += (s.id -> ref)
      }
    }

    restRef ! strategies
  }

}

object StrategiesManagerActor {
  def props = Props(classOf[StrategiesManagerActor])

  val path = "StrategiesManager"
}
