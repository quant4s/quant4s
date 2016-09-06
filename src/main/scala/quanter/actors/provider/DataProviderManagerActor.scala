/**
  *
  */
package quanter.actors.provider

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import quanter.config.Settings

/**
  *
  */
class DataProviderManagerActor extends Actor with ActorLogging {
  private  var real_provider: ActorRef = null

  _init()

  override def receive: Receive = {
    case _ =>
  }

  /**
    * 读取配置文件， 初始化数据提供器
    */
  private def _init(): Unit = {
    val settings = Settings(context.system)
    val clazz = Class.forName(settings.real_dataprovider)
    val path = settings.real_path
    real_provider = context.actorOf(Props(clazz), path)
  }
}

object DataProviderManagerActor {
  def prop: Props = {
    Props(classOf[DataProviderManagerActor])
  }

  def path = "dpm"
}
