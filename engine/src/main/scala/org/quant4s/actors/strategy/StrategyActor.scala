/**
  *
  */
package org.quant4s.actors.strategy

import akka.actor.{ActorLogging, ActorSelection, FSM, Props}
import org.quant4s.actors._
import org.quant4s.actors.persistence.{OrderPersistorActor, StrategyPersistorActor}
import org.quant4s.actors.strategy.StrategyActor._
import org.quant4s.actors.trade.{OrderDealResult, OrderStatusResult}
import org.quant4s.rest._
import org.quant4s.risk.BaseRisk

import scala.collection.mutable

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
    case Event(StartStrategy(tid), _) => {  // 启动策略
      log.debug("启动策略,编号为:%d".format(id))
      goto(Running)
    }
  }
  when(Running) {
    case Event(PauseStrategy(sid), _) => {  // 暂停策略
      _pauseStrategy(id)
      goto(Suspended)
    }
    case Event(t: Transaction, StrategyData(true)) => { // 打开风险控制模式下 接收订单
      log.debug("策略%d, 接受到订单".format(id))
      _handleOrderWithRiskControl(t)
      stay()
    }
    case Event(t: Transaction, StrategyData(false)) => {  // 关闭风险控制模式下，接收订单
      _handleOrderWithoutRiskControl(t)
      stay()
    }
    case Event(OpenRiskControl(sid), StrategyData(false)) => {  // 切换进入风险控制监护模式
      log.debug("策略%d, 启动风险控制".format(id))
      stay() using stateData.copy(riskControll = true)
    }
    case Event(CloseRiskControl(sid), StrategyData(true)) => {  // 切换退出风险控制监护模式
      log.debug("策略%d, 关闭风险控制".format(id))
      stay() using stateData.copy(riskControll = false)
    }
  }
  when(Stopped) {
    case Event(StartStrategy(sid), _) => {  // 启动策略
      if (stateData.riskControll) goto(RunningWithRiskControl)
      else goto(Running)
    }
  }
  when(Suspended) {
    case Event(RestoreStrategy(sid), _) => {    // 恢复策略
      _restoreStrategy(id)
      if (stateData.riskControll) goto(RunningWithRiskControl)
      else goto(Running)
    }
  }
  whenUnhandled {
    case Event(GetStrategy(sid), _) => {    // 获取策略描述
      strategyPersisRef ! new GetStrategy(id)
      stay()
    }
    case Event(UpdateStrategy(strategy), _) => {  // 更新策略
      strategyPersisRef ! new UpdateStrategy(strategy)
      stay()
    }
    case Event(UpdateRiskControlInfo, _) => stay()  // 更新风险控制
    case Event(UpdateTradeAccount, _) => stay()
    case Event(AddRisk(risk, rule), _) => {     // 加入风控规则
      _addRiskRule(risk, rule)
      stay()
    }
    case Event(StopStrategy, _) => {    // 停止策略运行
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

  /**
    * 处理风控委托单
    * @param tran 委托单
    */
  private def _handleOrderWithRiskControl(tran: Transaction): Unit = {
    for (order <- tran.orders.get) {
      val rc =  _riskMatch(order)
      if(rc) {
        log.info("风控禁止买入")
        // 写入风控日志
      } else {
        // 下单
        _handleOrder(order)
      }
    }

  }

  /**
    * 处理委托单，没有风控
    * @param tran 委托单
    */
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
    * 将订单发送给交易通道
    *
    * @param order
    */
  private def _handleOrder(order: Order): Unit = {
    order.strategyId = id

    // TODO: 修改发送到TradeRouteActor 发送到相应的交易接口， 交易
    _getBrokerageActor(order.tradeAccountId) ! order

    // 持久化
    orderPersisRef ! new NewOrder(order)
    log.debug("接收到策略%d订单%d, 交易接口为%d".format(id, order.orderNo, order.tradeAccountId))
  }

  /**
    * 将取消订单发送到交易通道
    * @param order
    */
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
  private def _getBrokerageActor(id: Int): ActorSelection = context.actorSelection("/user/%s/%d".format(PATH_TRADE_ROUTER_ACTOR, id))


  /**
    * 增加风控规则
    * @param riskName 风控类名
    * @param riskRule 风控规则
    */
  private def _addRiskRule(riskName: String, riskRule: String): Unit = {
    val clazz: String = "quanter.risk." + riskName
    val risk = Class.forName(clazz).newInstance().asInstanceOf[BaseRisk]
    risk.addRule(riskRule)

    if(!riskRules.contains(riskRule))
      riskRules += (riskRule ->risk)
  }

  /**
    * 风控检查
    * @param order 订单
    * @return
    */
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
