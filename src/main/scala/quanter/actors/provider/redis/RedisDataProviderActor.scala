/**
  *
  */
package quanter.actors.provider.redis

import java.net.InetSocketAddress

import akka.actor.ActorSelection
import quanter.actors.AskListenedSymbol
import quanter.actors.provider.DataProviderActor
import quanter.actors.securities.SecuritiesManagerActor
import quanter.securities.Security
import redis.actors.RedisSubscriberActor
import redis.api.pubsub.{Message, PMessage}

import scala.collection.mutable

/**
  *
  */
class RedisDataProviderActor (address: InetSocketAddress,channels: Seq[String], patterns: Seq[String], password: Option[String])
  extends RedisSubscriberActor(
    address,
    channels,
    patterns,
    password,
    onConnectStatus = connected => { println(s"连接状态: $connected")}) {

  def this() {
    this(new InetSocketAddress("172.16.240.1", 6379), Nil, Seq("qutation.*"), Some("1"))
  }

  var symbolSelections = new mutable.HashMap[String, ActorSelection]

  override def receive = super.receive orElse symbols

  def symbols:Receive = {
    case ask: AskListenedSymbol => addSymbol(ask.symbol)
  }

  /**
    * 接收订阅的主题消息
    * @param message
    */
  def onMessage(message: Message) {
    // TODO: 根据 redis 传输的内容，设置data
    val data = message.data.decodeString("utf8")
    println(s"message received: $data")

    // todo: 保留历史ticket
    // 写文件 是个耗时操作， 采用round-pool
  }

  /**
    * 接收订阅的模式消息
    * @param pmessage
    */
  def onPMessage(pmessage: PMessage) {
    println(s"pattern message received: $pmessage")
  }

  protected def addSymbol(symbol: String): Unit = {
//    val sec = new Security(symbol)
//    smRef ! sec
    if (!symbolSelections.contains(symbol)) {
      log.debug(s"准备接受${symbol}的行情数据")
      val ref = context.actorSelection(s"/user/${SecuritiesManagerActor.path}/${symbol}")
      symbolSelections += (symbol -> ref)
    }
  }
}
