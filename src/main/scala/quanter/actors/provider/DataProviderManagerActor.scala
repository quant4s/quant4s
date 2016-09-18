/**
  *
  */
package quanter.actors.provider

import java.util.HashMap

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import quanter.config.Settings

import scala.collection.mutable

/**
  *
  */
class DataProviderManagerActor extends Actor with ActorLogging {
  private val _providerRef = new mutable.HashMap[String, ActorRef]


  _init()

  override def receive: Receive = {
    case _ =>
  }

  /**
    * 读取配置文件， 初始化数据提供器
    */
  private def _init(): Unit = {
    val setting = Settings(context.system)
    for(i <- 0 until setting.providers.size()) {
      val provider = setting.providers.get(i)
      val name = (provider.asInstanceOf[HashMap[String, String]]).get("name")
      val clazz = (provider.asInstanceOf[HashMap[String, String]]).get("provider")
      val path =(provider.asInstanceOf[HashMap[String, String]]).get("path")

      val ref = context.actorOf(Props(Class.forName(clazz)), path)
      _providerRef.put(path, ref)
    }

  }
}

object DataProviderManagerActor {
  def prop: Props = {
    Props(classOf[DataProviderManagerActor])
  }

  def path = "dpm"

  sealed trait ProviderState

  case object Ready extends ProviderState
  case object Disconnected extends ProviderState
  case object Opened extends ProviderState
  case object Pause extends ProviderState

  case class BrokerageData()
}
