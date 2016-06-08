package quanter.actors.receivers

import java.io.{BufferedReader, InputStreamReader}

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import quanter.actors.securities.SecuritiesManagerActor
import quanter.data.market.SnapData

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class AskListenedSymbol(symbol: String)
case class QuerySnapData()
case class Execute()
/**
  *
  */

object SinaL1Actor {
  def props(): Props = {
    Props(classOf[SinaL1Actor])
  }
}

class SinaL1Actor extends Actor with ActorLogging {
  var symbolSelections = new mutable.HashMap[String, ActorSelection]
  var aliases = new ArrayBuffer[String]()
   context.system.scheduler.schedule(0 seconds, 3 seconds, self, new QuerySnapData())
  override def receive: Receive =  {
    case ask: AskListenedSymbol => _addSymbol(ask.symbol)
    case query: QuerySnapData => _querySnapData()
    case Execute => _run()
    case _ =>
  }

  private def _run(): Unit = {
    // TODO: 每3秒钟发出一个查询股价请求
     context.system.scheduler.schedule(0 seconds, 3 seconds, self, new QuerySnapData())
  }

  private def _addSymbol(symbol: String): Unit = {
    if (!symbolSelections.contains(symbol)) {
      log.debug(s"准备接受${symbol}的行情数据")
      val ref = context.actorSelection(s"/usr/${SecuritiesManagerActor.Path}/${symbol}")
      symbolSelections += (symbol -> ref)
      aliases += _symbol2Alias(symbol)
    }
  }

  private def _symbol2Alias(symbol: String): String = {
    val arr = symbol.split("\\.")
    arr(1) match {
      case "XSHE" => s"sz${arr(0)}"
      case "XSHG" => s"sh${arr(0)}"
    }
  }

  private def _alias2Symbol(alias: String): String = {
    val prefix = alias.substring(0,2)
    val code =  alias.substring(2)
    prefix match {
      case "sz" => s"${code}.XSHE"
      case "sh" => s"${code}.XSHG"
    }
  }

  /**
    * 根据Aliases 别名来查询， 没n（50）个 别名构成一组查询
    */
  private def _querySnapData(): Unit = {
    val aliasArr = _preProcessAlias

    for(s <- aliasArr) {
      // 从sina查询快照数据， 获取到数据 转成SnapData
      val url = s"http://hq.sinajs.cn/list=${s}"
      println(url)
      val client = new DefaultHttpClient()
      val request = new HttpGet(url)

      val response = client.execute(request)
      val rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

      var line = "";
      while ((line = rd.readLine()) != null) {
        println(line)
        // 分析每行数据转为SnapData
        val data = _parseLine(line)
        _newDataArrived(data)
      }
    }
  }

  private def _preProcessAlias = {
    val n = 50
    var aliasArr = new ArrayBuffer[String]()
    for(x <- aliases) {
      aliasArr += x
    }
    aliasArr.toArray
  }

  private def _parseLine(line: String): SnapData = {
    // TODO:

    val data = new SnapData()
    val symbol = line.substring(11, 19)
    data.symbol= _alias2Symbol(symbol)

    data
  }

  private def _newDataArrived(data: SnapData): Unit = {
    symbolSelections(data.symbol) ! data
  }

}
