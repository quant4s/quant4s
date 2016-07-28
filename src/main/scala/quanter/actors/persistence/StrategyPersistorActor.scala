/**
  *
  */
package quanter.actors.persistence

import akka.actor.Actor
import quanter.rest._
import quanter.strategies.StrategyCache

/**
  *
  */
class StrategyPersistorActor extends  Actor{
  val strategyCache = new StrategyCache()
  val ref = context.actorSelection("/user/" + PersistenceActor.path)

  override def receive: Receive = ???


  def insertStrategy(strategy: Strategy): Unit = {
    strategyCache.addStrategy(strategy)

    // 调用slick
    ref ! SaveStrategy(strategy)
  }

  def getStrategy(id: Int): Unit = {
    strategyCache.getStrategy(id)
  }


}
