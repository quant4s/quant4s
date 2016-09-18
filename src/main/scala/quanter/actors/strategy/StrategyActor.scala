/**
  *
  */
package quanter.actors.strategy

import akka.actor.{ActorLogging, ActorSelection, FSM, Props}
import quanter.actors.persistence.PersistenceActor
import quanter.actors._
import quanter.actors.strategy.StrategyActor._
import quanter.actors.trade.TradeRouteActor
import quanter.rest._
import quanter.risk.BaseRisk

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * 1、更新策略
  * 2、处理订单
  * 3、风险控制
  * 4、资金组合调整
  */
class StrategyActor(id: Int) extends FSM[StrategyState, StrategyData] with ActorLogging {
  val persisRef = context.actorSelection("/user/" + PersistenceActor.path)
  val strategyContext: StrategyContext = null
  val riskRules = new ArrayBuffer[BaseRisk]()

  // TODO: 从数据库中读取1、持仓信息 2、当日成交信息 3、当日委托信息
  val portfolio: Portfolio = null

  val rc = true
  startWith(Initialized, new StrategyData(riskControll = rc))

  when(Initialized) {
    case Event(StartStrategy, _) =>
      goto(Running)
  }
  when(Running) {
    case Event(PauseStrategy(id), _) =>
      _pauseStrategy(id)
      goto(Suspended)
    case Event(t: Transaction, _) =>
      _handleOrder(t)
      stay()
    case Event(OpenRiskControl, _) =>
      goto(RunningWithRiskControl) using stateData.copy(riskControll = true)
  }
  when(RunningWithRiskControl) {
    case Event(t: Transaction, _) =>
      _handleOrder(t)
      stay()
    case Event(CloseRiskControl, _) => goto(Running) using stateData.copy(riskControll = false)
  }
  when(Stoped) {
    case Event(StartStrategy, _) =>
      if(stateData.riskControll) goto(RunningWithRiskControl)
      else goto(Running)
  }
  when(Suspended) {
    case Event(RestoreStrategy(id), _) =>
      _restoreStrategy(id)
      if(stateData.riskControll) goto(RunningWithRiskControl)
      else goto(Running)
  }
  whenUnhandled {
    case Event(GetStrategy(id), _) =>
      _getStrategy(id)
      stay()
    case Event(UpdateStrategy(id), _) =>
      _updateStrategy(id)
      stay()
    case Event(UpdateRiskControlInfo, _) =>
      stay()
    case Event(UpdateTradeAccount, _) =>
      stay()
    case Event(AddRisk(risk, rule), _) =>
      _addRiskRule(risk, rule)
      stay()
  }

  /**
    * 更新策略
    *
    * @param strategy
    */
  private def _updateStrategy(strategy: Strategy) = {
    // TODO: 处理cache
    //    strategyCache.updateStrategy(strategy)
    persisRef ! UpdateStrategy(strategy)
  }

  /**
    * 获取策略, 包括当日持仓信息，当日委托信息，当日成交信息
    *
    * @param id
    */
  private def _getStrategy(id: Int): Unit = {
    // TODO: 从数据库中获取数据
    //  println ("长度为："+ managers.getAllStrategies().length)
    //    val strategy = strategyCache.getStrategy(id)
    //    val portfolio = Portfolio(10.0, new Date(), None)
    // strategy.get.portfolio = Some(portfolio)
    //    sender ! strategy
  }


  /**
    * 将订单发送给合适的交易通道
    *
    * @param tran
    */
  private def _handleOrder(tran: Transaction): Unit = {
    if(tran.orders != None) {
      for (order <- tran.orders.get) {
        if(stateData.riskControll){
          _riskMatch(order)
        }

        order.strategyId = tran.strategyId
        // 发送到相应的交易接口
        persisRef ! new NewOrder(order)
        _getBrokerageActor(order.tradeAccountId) ! order
        log.debug("接收到策略%d订单%d, 交易接口为%d".format(order.strategyId, order.orderNo, order.tradeAccountId))
      }
    }

    if(tran.cancelOrder != None) {
      val accountId = 0
      val order = tran.cancelOrder.get
      order.strategyId = tran.strategyId
      // 将取消订单保存到数据库，并发送到交易接口
      persisRef ! new RemoveOrder(order)
      _getBrokerageActor(accountId) ! order
      log.info("取消策略%d订单%d,交易接口为%d".format(order.strategyId, order.cancelOrderNo, order.tradeAccountId))
    }
  }

  private def _pauseStrategy(id: Int) = {}
  private def _restoreStrategy(id: Int) = {}
  private def _getBrokerageActor(id: Int): ActorSelection = context.actorSelection("/user/%s/%d".format(TradeRouteActor.path, id))

  /**
    * 增加风控
    */
  private def _addRiskRule(riskName: String, riskRule: String): Unit = {
    // 动态创建
    val clazz: String = "quanter.risk." + riskName
    val risk = Class.forName(clazz).newInstance().asInstanceOf[BaseRisk]
    risk.addRule(riskRule)

    riskRules += risk
  }
  
  private def _riskMatch(order: Order): Boolean = {
    var ret = false
    for(risk <- riskRules) {
      if(risk.matchRule(portfolio, order)) ret = true
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
  case object Stoped extends StrategyState
  case object RunningWithRiskControl extends StrategyState

  case class StrategyData(riskControll: Boolean = false)

  class StrategyContext {

  }
}
