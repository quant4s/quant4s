/**
  *
  */
package org.quant4s.actors.securitySelection

import java.io.File

import akka.actor.{Actor, ActorLogging}
import com.github.tototoshi.csv.CSVReader
import org.quant4s.TimeSpan
import org.quant4s.mds.data.{DataManagerActor, RequestBarData, RequestTickData}
import org.quant4s.data.BaseData
import org.quant4s.mds.SubscriptionSymbol
import org.quant4s.data.market.TradeBar
import org.quant4s.mds.SecuritiesManagerActor
import org.quant4s.mds.data.DataManagerActor
import org.quant4s.zeromq.ZeroMQSubPubServerActor

import scala.collection.mutable.HashMap
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * 小市值选股器
  * 1、请求全市场股票的5f Bar数据，进行计算
  */
class SmallMarketValueCSMActor(count: Int, span: TimeSpan) extends Actor with ActorLogging{
  var securities = new HashMap[String,(Long, Long)]() // symbol, capital, market value
  var smv = new HashMap[String, (Long, Long)]()
  val securityManagerRef = context.actorSelection("/user/%s".format(SecuritiesManagerActor.path))
  val pubRef = context.actorSelection("/user/" + ZeroMQSubPubServerActor.path)
  val dataManager = context.actorSelection("/user/" + DataManagerActor.path)

  override def receive: Receive = {
    case d: TradeBar => _computeMarketValue(d)
//    case Compute => _sort()
  }

  // 请求Bar 数据
  def _init(): Unit = {
    // 读取股票数据信息, 市值计算，排序市值排名靠后的 关注的数量 +50个
    val file = new File("stock.list.csv")
    val reader = CSVReader.open(file)
    val symbols = reader.allWithHeaders()
    symbols.foreach(m => {
      val symbol = m("代码")
      val close = m("收盘价").toDouble
      val capital = m("总股本").toLong
      if(close != 0.0)
        securities += (symbol -> (capital, (close * capital).toLong))
    })
    smv ++= securities.toArray.sortBy(_._2._2).take(count + 50).toMap

    // TODO: 订阅价格，计算
    for(s <- smv.keys)
      dataManager ! RequestBarData(s, s)  //

    // 定时计算股票市值 并计算排序
    // context.system.scheduler.schedule(0 seconds, 3 seconds, self, new Compute())
  }

  def _sort() : Unit = {
    val c = smv.toArray.sortBy(_._2).take(count).toMap.values
    println()
  }

  def _computeMarketValue(data: BaseData): Unit = {
    val capital = smv.get(data.symbol).get._1
    smv(data.symbol) = (capital, (data.value * capital).toLong)

  }
}

object SmallMarketValueCSMActor {
  case class MarketValue(capital: Long, marketValue: Long)
}



