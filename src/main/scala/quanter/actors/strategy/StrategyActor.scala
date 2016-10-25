/**
  *
  */
package quanter.actors.strategy

import akka.actor.{ActorLogging, ActorSelection, FSM, Props}
import akka.pattern.ask
import akka.util.Timeout
import quanter.actors.persistence.PersistenceActor
import quanter.actors._
import quanter.actors.strategy.StrategyActor._
import quanter.actors.trade.TradeRouteActor
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
  val persisRef = context.actorSelection("/user/" + PersistenceActor.path)
  val restRef = context.actorSelection("/user/" + HttpServer.path)
  val strategyContext: StrategyContext = null
  val riskRules = new mutable.HashMap[String, BaseRisk]()

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
    case Event(t: Transaction, _) => {    // 逐条处理订单的时候，进行风控检查
      log.debug("策略%d, 接受到订单".format(id))
      _handleOrder(t)
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
      persisRef ! new GetStrategy(id)
      stay()
    }
    case Event(UpdateStrategy(strategy), _) => {
      persisRef ! new UpdateStrategy(strategy)
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
  }

  /**
    * 将订单发送给合适的交易通道
    *
    * @param tran
    */
  private def _handleOrder(tran: Transaction): Unit = {
    if(tran.orders != None) {
      for (order <- tran.orders.get) {
        var rc = false
        if(stateData.riskControll){
         rc = _riskMatch(order)
        }

        if(rc) {
          log.info("风控禁止买入")
        } else {
          order.strategyId = id
          // 发送到相应的交易接口
          _getBrokerageActor(order.tradeAccountId) ! order
          persisRef ! new NewOrder(order)
          log.debug("接收到策略%d订单%d, 交易接口为%d".format(id, order.orderNo, order.tradeAccountId))
        }
      }
    }

    if(tran.cancelOrder != None) {
      val accountId = 0
      val order = tran.cancelOrder.get
      order.strategyId = tran.strategyId
      // 将取消订单保存到数据库，并发送到交易接口
      _getBrokerageActor(order.tradeAccountId) ! order
      persisRef ! new RemoveOrder(order)
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
