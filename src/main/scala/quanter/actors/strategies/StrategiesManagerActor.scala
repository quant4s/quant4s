/**
  *
  */
package quanter.actors.strategies

import akka.actor.{Actor, Props}
import quanter.rest.Strategy
import quanter.strategies.StrategiesManager

case class CreateStrategy(strategy: Strategy)
case class UpdateStrategy(strategy: Strategy)
case class DeleteStrategy(id: Int)
case class RunStrategy(id: Int)

/**
  *
  */
class StrategiesManagerActor extends Actor{
  val managers = new StrategiesManager()

  override def receive: Receive = {
    case s: CreateStrategy => _saveStrategy(s.strategy)
    case s: UpdateStrategy => _updateStrategy(s.strategy)
    case s: DeleteStrategy => _deleteStrategy(s.id)
    case s: RunStrategy => _runStrategy(id)
  }

  private def _saveStrategy(strategy: Strategy) = {
    // TODO: 保存到数据库
    managers.addStrategy(strategy)
  }

  private def _updateStrategy(strategy: Strategy) = {
    // TODO: 保存到数据库
    managers.modifyStrategy(strategy)
  }

  private def _deleteStrategy(id: Int) = {
    // TODO: 保存到数据库
    managers.removeStrategy(id)
  }

  private def _runStrategy(id: Int): Unit = {
    val strategy = managers.getStrategy(id)
    strategy.status = 1

    // TODO: 保存到数据库
  }
}

object StrategiesManagerActor {
  def props = {
    Props(classOf[StrategiesManagerActor])
  }

  val path = "StrategiesManager"
}
