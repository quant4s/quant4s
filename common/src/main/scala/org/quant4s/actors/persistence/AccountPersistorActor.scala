/**
  *
  */
package org.quant4s.actors.persistence

import akka.actor.Props
import org.quant4s.actors.{DeleteTrader, ListTraders, NewTrader, UpdateTrader}
import org.quant4s.rest.TradeAccount
import org.quant4s.persistence.{ETrader, TraderDao}

import scala.collection.mutable.ArrayBuffer

/**
  *
  */
class AccountPersistorActor extends BasePersistorActor{
  val traderDao = new TraderDao

  override def receive: Receive = {
    // 交易接口
    case action: NewTrader => _saveTrader(action.trader)
    case action: ListTraders => _listTraders()
    case action: DeleteTrader => _deleteTrader(action.id)
    case action: UpdateTrader => _updateTrader(action.trader)

  }

  private def _saveTrader(trader: TradeAccount): Unit = {
    val t = ETrader(None, trader.name, trader.brokerType, trader.brokerName, trader.brokerCode, trader.brokerAccount, trader.brokerPassword, trader.brokerUri, trader.brokerServicePwd, trader.status)
    val s1 = traderDao.insert(t)

    val t1 = TradeAccount(s1.id, s1.name, s1.brokerType, s1.brokerName, s1.brokerCode, s1.brokerAccount, s1.brokerPassword, s1.brokerUri, s1.brokerServicePwd, s1.status)
    sender ! t1
  }

  private def  _listTraders(): Unit = {
    val traderArr = new ArrayBuffer[TradeAccount]()
    for(s <- traderDao.list) {
      val trader = TradeAccount(s.id, s.name, s.brokerType, s.brokerName, s.brokerCode, s.brokerAccount, s.brokerPassword, s.brokerUri, s.brokerServicePwd, s.status)
      traderArr += trader
    }

    sender ! traderArr.toArray
  }

  private def _updateTrader(trader: TradeAccount): Unit = {
    val s = ETrader(trader.id, trader.name,trader.brokerType, trader.brokerName, trader.brokerCode, trader.brokerAccount, trader.brokerPassword, trader.brokerUri, trader.brokerServicePwd, trader.status)
    val query = traderDao.update(s.id.get, s)
  }

  private def _deleteTrader(id: Int): Unit = {
    traderDao.delete(id)
  }
}

object AccountPersistorActor {
  def props = {
    Props(classOf[AccountPersistorActor])
  }

  val path = "account_persistence"
}
