/**
  *
  */
package quanter.actors.scheduling

import akka.actor.{Actor, ActorLogging}
import quanter.actors.data.{DataManagerActor, RequestIndicatorData, Subscribe}

/**
  * 获取指标
  */
class DailyDataJob extends BaseJob {
  val dataManager = context.actorSelection("/user/" + DataManagerActor.path)


  def _init: Unit = {
    // 订阅MA5，MA10, MA15 技术指标
    dataManager ! new Subscribe("symbol,day,dmai,1~4")
  }

  override def executeJob(): Unit = ???
}
