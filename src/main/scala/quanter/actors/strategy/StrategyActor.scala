/**
  *
  */
package quanter.actors.strategy

import akka.actor.{ActorLogging, ActorSelection, FSM, Props}
import akka.pattern.ask
import akka.util.Timeout
import quanter.actors.persistence.{OrderPersistorActor, PersistenceActor, StrategyPersistorActor}
import quanter.actors._
import quanter.actors.strategy.StrategyActor._
import quanter.actors.trade.{OrderDealResult, OrderStatusResult, TradeRouteActor}
import quanter.rest._
import quanter.risk.BaseRisk

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * 1、更新策略
  * 2、处理订单
  * 3、风险控制
  * 4、资金组合调整
  */
class StrategyActor(id: Int) extends FSM[StrategyState, StrategyData] with ActorLogging {
  val orderPersisRef = context.actorSelection("/user/" + OrderPersistorActor.path)
  val strategyPersisRef = context.actorSelection("/user/" + StrategyPersistorActor.path)
  val restRef = context.actorSelection("/user/" + HttpServer.path)
  val strategyContext: StrategyContext = null
  val riskRules = new mutable.HashMap[String, BaseRisk]()
  val accountRef = Array[ActorSelection]()    // TODO: 从数据库中读取策略绑定的账户信息

  // TODO: 从数据库中读取1、持仓信息 2、当日成交信息 3、当日委托信息
  val portfolio: Portfolio = null

  val rc = true
  startWith(Initialized, new StrategyData(riskControll = rc))

  when(Initialized) {
    case Event(StartStrategy(tid), _) => {
      log.debug("启动策略,编号为:%d".format(id))
      goto(Running)
    }
  }
  when(Running) {
    case Event(PauseStrategy(sid), _) => {
      _pauseStrategy(id)
      goto(Suspended)
    }
    case Event(t: Transaction, StrategyData(true)) => {
      log.debug("策略%d, 接受到订单".format(id))
      _handleOrderWithRiskControl(t)
      stay()
    }
    case Event(t: Transaction, StrategyData(false)) => {
      _handleOrderWithoutRiskControl(t)
      stay()
    }
    case Event(OpenRiskControl(sid), StrategyData(false)) => {
      log.debug("策略%d, 启动风险控制".format(id))
      stay() using stateData.copy(riskControll = true)
    }
    case Event(CloseRiskControl(sid), StrategyData(true)) => {
      log.debug("策略%d, 关闭风险控制".format(id))
      stay() using stateData.copy(riskControll = false)
    }
  }
  when(Stopped) {
    case Event(StartStrategy(sid), _) => {
      if (stateData.riskControll) goto(RunningWithRiskControl)
      else goto(Running)
    }
  }
  when(Suspended) {
    case Event(RestoreStrategy(sid), _) => {
      _restoreStrategy(id)
      if (stateData.riskControll) goto(RunningWithRiskControl)
      else goto(Running)
    }
  }
  whenUnhandled {
    case Event(GetStrategy(sid), _) => {
      strategyPersisRef ! new GetStrategy(id)
      stay()
    }
    case Event(UpdateStrategy(strategy), _) => {
      strategyPersisRef ! new UpdateStrategy(strategy)
      stay()
    }
    case Event(UpdateRiskControlInfo, _) => stay()
    case Event(UpdateTradeAccount, _) => stay()
    case Event(AddRisk(risk, rule), _) => {
      _addRiskRule(risk, rule)
      stay()
    }
    case Event(StopStrategy, _) => {
      if(stateData.riskControll) goto(RunningWithRiskControl)
      else goto(Running)
    }
    case Event(s:Strategy, _) => {
      restRef ! s
      stay
    }
    case Event(r: OrderStatusResult, _) => {
      _updateOrderStatus(r)
      stay()
    }
    case Event(r: OrderDealResult, _) => {
      _insertExecRpt(r)
      stay()
    }
  }

  private def _handleOrderWithRiskControl(tran: Transaction): Unit = {
    for (order <- tran.orders.get) {
      var rc =  _riskMatch(order)
      if(rc) {
        log.info("风控禁止买入")
        // 写入风控日志
      } else {
        // 下单
        _handleOrder(order)
      }
    }

  }

  private def _handleOrderWithoutRiskControl(tran: Transaction): Unit = {
    if(tran.orders != None) {
      for (order <- tran.orders.get) {
        // 下单
        _handleOrder(order)
      }
    }

    if(tran.cancelOrder != None) {
      _handleCancelOrder(tran.cancelOrder.get)
    }
  }


  /**
    * 将订单发送给合适的交易通道
    *
    * @param order
    */
  private def _handleOrder(order: Order): Unit = {
    order.strategyId = id

    // 发送到相应的交易接口， 交易
    _getBrokerageActor(order.tradeAccountId) ! order

    // 持久化
    orderPersisRef ! new NewOrder(order)
    log.debug("接收到策略%d订单%d, 交易接口为%d".format(id, order.orderNo, order.tradeAccountId))
  }

  private def _handleCancelOrder(order: CancelOrder): Unit = {
    // 将取消订单保存到数据库，并发送到交易接口
    _getBrokerageActor(order.tradeAccountId) ! order
    log.info("取消策略%d订单%d,交易接口为%d".format(order.strategyId, order.strategyId, order.tradeAccountId))
  }

  private def _updateOrderStatus(r: OrderStatusResult): Unit = {
    // orderPersisRef
    log.debug("[StrategyActor._updateOrderStatus]更新委托单状态")
    orderPersisRef ! r
  }

  /**
    * 插入成交回报
    */
  private def _insertExecRpt(r: OrderDealResult): Unit = {
    log.debug("[StrategyActor._insertExecRpt]Strategy: %d, 插入交易回报".format(id))
    orderPersisRef ! r
  }

  private def _pauseStrategy(id: Int) = {}
  private def _restoreStrategy(id: Int) = {}
  private def _getBrokerageActor(id: Int): ActorSelection = context.actorSelection("/user/%s/%d".format(TradeRouteActor.path, id))

  /**
    * 增加风控
    */
  private def _addRiskRule(riskName: String, riskRule: String): Unit = {
    val clazz: String = "quanter.risk." + riskName
    val risk = Class.forName(clazz).newInstance().asInstanceOf[BaseRisk]
    risk.addRule(riskRule)

    if(!riskRules.contains(riskRule))
      riskRules += (riskRule ->risk)
  }

  private def _riskMatch(order: Order): Boolean = {
    var ret = false
    for(risk <- riskRules) {
      if(risk._2.matchRule(strategyContext, order)) ret = true
    }
    ret
  }
}

object StrategyActor {
  def props(id: Int) = Props(classOf[StrategyActor], id)

  sealed trait StrategyState

  case object Initialized extends StrategyState
  case object Running extends StrategyState
  case object Suspended extends StrategyState
  case object Stopped extends StrategyState
  case object RunningWithRiskControl extends StrategyState

  case class StrategyData(riskControll: Boolean = false)

  case class StrategyContext(portfolio: Portfolio)
}
