package quanter.rest


import akka.actor.Actor.Receive
import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import org.json4s.jackson.JsonMethods._
import org.json4s.{DefaultFormats, Extraction, Formats}
import quanter.actors._
import quanter.actors.trade._
import quanter.config.Settings
import spray.routing.HttpService
import spray.util.LoggingContext

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * 交易账户服务，接受两个地方的消息
  * 1. web service 的消息
  * 2. trade router factor 反馈的消息
  *   a. 一个新的交易账户对象
  *   b. 状态消息（10：连接成功|11：失败，20：登录成功|21：失败）
  */
trait TradeAccountService extends HttpService {
  implicit def systemRef: ActorSystem

  val traderManager = actorRefFactory.actorSelection("/user/" + TradeRouteActor.path)
  var accountsCache = new mutable.HashMap[Int, TradeAccount]()
  val channelTypes = _initChannelTypes()

  // 获取列表
  traderManager ! new ListTraders()

  def tradeAccountServiceRoute(implicit log: LoggingContext)  = {
    get {
      path("account" / "list") {
        complete {
          // 将交易账户数组转换成Json字符串
//          if(tradeAccountCache.size == 0) {
//            traderManager ! new ListTraders()
//          }

          val retTraders = RetTradeAccountList(0, "success", if(accountsCache.size != 0) Some(accountsCache.values.toArray) else None)
          implicit val formats: Formats = DefaultFormats
          val json = Extraction.decompose(retTraders)
          compact(render(json))
        }
      }~
      path("config" / "brokerageChannelTypes") {
        complete {
          // 将通道类型对象转换成Json字符串
          implicit val formats: Formats = DefaultFormats
          val json = Extraction.decompose(channelTypes)
          compact(render(json))
        }
      }
    } ~
    post {
      path("account") {   // 创建交易账户
        requestInstance {
          request => {
            complete {
              _createTrader(request.entity.data.asString)
            }
          }
        }
      }
    } ~
    put {
      path("account" / IntNumber) { // 更新交易账户
        id => {
          requestInstance {
            request => {
              complete {
                _updateTrader(request.entity.data.asString)
              }
            }
          }
        }
      }~
      path("account" / "connect" / IntNumber) {
        id => {
          complete {
            _reconnect(id)
          }
        }
      }
    } ~
    delete {
      path("account" / IntNumber) {
        id => {
          complete {
             _deleteTrader(id)
          }
        }
      }
    }
  }

  def respTradeAccountReceive(implicit log: LoggingContext): Receive = {
    case t: Array[TradeAccount] => {
      _buildTraderAccountCache(t)
    }
    case t: TradeAccount => {
      _updateTraderAccountCache(t)
    }
  }

  protected def _buildTraderAccountCache(accounts: Array[TradeAccount])(implicit log: LoggingContext) : Unit = {
    log.debug("在Rest Service层创建交易账号缓存")
    for(acc <- accounts) {
       accountsCache +=(acc.id.get -> acc)
    }
  }

  protected def _updateTraderAccountCache(acc: TradeAccount)(implicit log: LoggingContext) : Unit = {
    log.debug("在Rest Service层更新交易账号缓存")
    accountsCache(acc.id.get) = acc
  }

  /**
    * 获取所有支持的通道类型
 *
    * @return
    */
  private def _initChannelTypes() = {
    val setting = Settings(systemRef)

    val channelTypes = new ArrayBuffer[ChannelType]()

    for(i <- 0 until setting.channelTypes.size()) {
      val provider = setting.channelTypes.get(i).asInstanceOf[java.util.HashMap[String, Any]]
      val name = provider.get("name").toString
      val title = provider.get("title").toString
      val desc = provider.get("desc").toString
      val driver = provider.get("driver").toString
      val _type = provider.get("type").toString

      val channelType = new ChannelType(_type, name, title, desc, driver)
      channelTypes += channelType
    }

    new RetChannelTypes(0, "", channelTypes.toArray)
  }

  private def _reconnect(id: Int): String = {
    val tradeAccountRef = actorRefFactory.actorSelection("/user/" + TradeRouteActor.path + "/" + id.toString)
    tradeAccountRef ! new Connect()
    """{"code": 0}"""
  }

  /**
    * 创建交易账户
 *
    * @param json
    * @return
    */
  private def _createTrader(json: String): String = {
    // 将JSON转换为strategy，加入到strategy
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val trader = jv.extract[TradeAccount]

      traderManager ! NewTrader(trader)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _updateTrader(json: String): String = {
    implicit val formats = DefaultFormats
    try {
      val jv = parse(json)
      val trader = jv.extract[TradeAccount]

      traderManager ! new UpdateTrader(trader)
      accountsCache.put(trader.id.get, trader)
      """{"code":0}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }

  private def _deleteTrader(id: Int)(implicit log: LoggingContext): String = {
    try {
      //      strategiesManager.removeStrategy(id)
      traderManager ! DeleteTrader(id)
      accountsCache.remove(id)
      """{"code":0, "message":"成功删除"}"""
    }catch {
      case ex: Exception => """{"code":1, "message":"%s"}""".format(ex.getMessage)
    }
  }
}
