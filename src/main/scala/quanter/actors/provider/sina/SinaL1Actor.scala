package quanter.actors.provider.sina

import java.io.{BufferedReader, InputStreamReader}
import java.util.Date

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.control.Breaks._
import akka.actor.{ActorLogging, Props}
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import quanter.actors.AskListenedSymbol
import quanter.actors.provider.{DataProviderActor, QuerySnapData}
import quanter.data.market.SnapData
import quanter.CommonExtensions._
import quanter.actors.scheduling.ExecuteJob

/**
  *
  */

object SinaL1Actor {
  def props: Props = {
    Props(classOf[SinaL1Actor])
  }

  def path = "sinal1"
}

class SinaL1Actor extends DataProviderActor with ActorLogging {
//  var symbolSelections = new mutable.HashMap[String, ActorSelection]
  var aliases = new ArrayBuffer[String]()
  addSymbol("000002.XSHE")
  addSymbol("000001.XSHE")

  log.info("启动Sina L1 行情获取......")
 // context.system.scheduler.schedule(0 seconds, 3 seconds, self, new QuerySnapData())

  override protected def addSymbol(symbol: String): Unit = {
    super.addSymbol(symbol)
    if (!symbolSelections.contains(symbol)) {
      aliases += _symbol2Alias(symbol)
    }
  }

  override protected def executeJob(): Unit = {
    _querySnapData()
    log.info("开始Sina L1 定时行情获取......")
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

    for(i <- 0 until aliasArr.length) {
      // 从sina查询快照数据， 获取到数据 转成SnapData
      val s = aliasArr(i)
      val url = s"http://hq.sinajs.cn/list=${s}"
      val client = new DefaultHttpClient()
      val request = new HttpGet(url)

      val response = client.execute(request)
      val rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

      var line = ""
      breakable {
        while ((line = rd.readLine()) != null) {
          if (line == null) break;
          // 分析每行数据转为SnapData
          val data = _parseLine(line)
          _newDataArrived(data)
        }
      }
     // }
    }
  }

  private def _preProcessAlias = {
    val n = 50
    var aliasArr = new ArrayBuffer[String]()
    var i = 0
    var tmps = "";
    for(x <- aliases) {
      i += 1;
      if( i >= 50) {
        i = 0
        aliasArr += tmps
        tmps = ""
      }
      tmps += x + ","
    }

    if(tmps != "")
      aliasArr += tmps

    aliasArr
  }

  private def _parseLine(line: String): SnapData = {
    val data = new SnapData()
    val items = line.split(",")

    val symbol = line.substring(11, 19)
    data.symbol= _alias2Symbol(symbol)

    data.open = items(1).toDouble
    data.close = items(3).toDouble
    data.high = items(4).toDouble
    data.low = items(5).toDouble
    data.time =  (items(30).toString() + " " + items(31).toString()).toDate("yyyy-MM-dd hh:mm:ss")

    data.update(items(3).toDouble, items(7).toDouble, items(6).toDouble, items(8).toLong, 0, 0)
    data
  }

  private def _newDataArrived(data: SnapData): Unit = {
     symbolSelections(data.symbol) ! data
  }

}
