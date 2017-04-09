/**
  *
  */
package org.quant4s.mds.provider

import java.util.HashMap

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.quant4s.actors.AskListenedSymbol

import org.quant4s.config.Settings

import scala.collection.mutable

/**
  *
  */
class DataProviderManagerActor extends Actor with ActorLogging {
  private val _providerRefs = new mutable.HashMap[String, ActorRef]

  init()

  override def receive: Receive = {
    case AskListenedSymbol(symbol) => addSymbol(symbol)
  }

  /**
    * 读取配置文件， 初始化数据提供器
    */
  def init(): Unit = {
    val setting = Settings(context.system)
    for(i <- 0 until setting.providers.size()) {
      val provider = setting.providers.get(i)
      val name = (provider.asInstanceOf[HashMap[String, String]]).get("name")
      val clazz = (provider.asInstanceOf[HashMap[String, String]]).get("provider")
      val path =(provider.asInstanceOf[HashMap[String, String]]).get("path")

      log.info("创建数据提供Actor： " +  clazz)
      val ref = context.actorOf(Props(Class.forName(clazz)), path)
      ref ! new ConnectDataProvider()
      _providerRefs.put(path, ref)
    }
  }

  def addSymbol(symbol: String): Unit = {
    for(provider <- _providerRefs.values) {
      provider ! new AskListenedSymbol(symbol)
    }
  }
}

object DataProviderManagerActor {
  def prop: Props = {
    Props(classOf[DataProviderManagerActor])
  }

  def path = "data-provider-manager"

  sealed trait ProviderState

  case object Ready extends ProviderState
  case object Disconnected extends ProviderState
  case object Opened extends ProviderState
  case object Pause extends ProviderState

  case class BrokerageData()
}
