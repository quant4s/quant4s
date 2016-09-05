package quanter.actors.provider.sina

import java.io.{BufferedReader, InputStreamReader}

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import quanter.actors.provider.{AskListenedSymbol, QuerySnapData}
import quanter.actors.securities.SecuritiesManagerActor
import quanter.data.market.SnapData

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.control.Breaks._


/**
  *
  */

object SinaL1Actor {
  def props: Props = {
    Props(classOf[SinaL1Actor])
  }

  def path = "sinafree"
}

class SinaL1Actor extends Actor with ActorLogging {
  var symbolSelections = new mutable.HashMap[String, ActorSelection]
  var aliases = new ArrayBuffer[String]()
  _addSymbol("000002.XSHE")
  _addSymbol("000001.XSHE")

  @scala.throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    super.preStart()
    log.info("启动Sina L1 行情获取......")
    _run()
  }

  override def receive: Receive =  {
    case ask: AskListenedSymbol => _addSymbol(ask.symbol)
    case query: QuerySnapData => _querySnapData()
    case _ =>
  }

  private def _run(): Unit = {
    context.system.scheduler.schedule(0 seconds, 3 seconds, self, new QuerySnapData())
//    context.system.scheduler.schedule(0 seconds, 3 seconds, new Runnable {
//      override def run(): Unit =  {
//        if(_isOpened) {
//          _querySnapData()
//        }
//      }
////      private def _isClosed = false
////      private def _isNotOpened = false
////      private def _isPause = false
//      private def _isOpened = true
//    });
  }

  private def _addSymbol(symbol: String): Unit = {
    if (!symbolSelections.contains(symbol)) {
      log.info(s"准备接受${symbol}的行情数据")
      val ref = context.actorSelection(s"/user/${SecuritiesManagerActor.path}/${symbol}")
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
    // TODO:
    val data = new SnapData()
    val items = line.split(",")

    val symbol = line.substring(11, 19)
    data.symbol= _alias2Symbol(symbol)

    data.open = items(1).toDouble
    data.close = items(3).toDouble
    data.high = items(4).toDouble
    data.low = items(5).toDouble

//    data.volume = items(8).toLong
//    data.turnover = items(9).toDouble
    data.update(items(3).toDouble, items(7).toDouble, items(6).toDouble, items(8).toLong, 0, 0)

    //bid.Name = items[0].Substring(21, items[0].Length - 21);    // var hq_str_sz150023="深成指B

    //bid.Buy = decimal.Parse(items[6]);
    //bid.Sell = decimal.Parse(items[7]);
    //bid.Volume = long.Parse(items[8]);
    //bid.Turnover = float.Parse(items[9]);


    data
  }

  private def _newDataArrived(data: SnapData): Unit = {
     symbolSelections(data.symbol) ! data
  }


}
