/**
  *
  */
package quanter.actors.trade

import java.util.Date


/**
  *
  * @param id 唯一标识
  * @param localID 本地委托号
  * @param mdDate 最新行情对应的时间(回报返回时行情里的时间可能不是当前时间)
  * @param accountID 资金账户ID(可能为空，因为仅是为了开发跟随策略追加的，在除成交回报外的回报对象中并没有设置此值)
  * @param direction 买卖方向(仅在做市应价单时使用，因为部分成交、全部成交是靠quant4s自己模拟，而不是从交易前置发出，
  *                  但在双边报价时，需要同时更新两个单子的状态，仅在成交回报返回时此值有效，其他状态如未成交还在队列中，则不需要此字段)
  * @param orderSysID 报单编号(由柜台生成，用于在柜台内部唯一标识这笔委托)
  */
abstract class OrderResult(id: String, localID: String, mdDate: Date, accountID: Option[String], direction: Int, orderSysID: String) {

}

case class OrderStatusResult(id: String, localID: String, mdDate: Date, accountID: Option[String], direction: Int, orderSysID: String, orderStatus: String)
  extends  OrderResult(id, localID,mdDate, accountID, direction, orderSysID)

case class OrderDealResult(id: String, localID: String, mdDate: Date, accountID: Option[String], contractCode: String,
                             direction: Int, OCType: Int, dealID: String, dealPrice: Double, dealVol: Int, orderSysID: String)
  extends OrderResult(id, localID,mdDate, accountID, direction, orderSysID)

