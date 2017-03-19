/**
  *
  */
package quanter.actors.trade

import java.util.Date


/**
  *
  * @param id 唯一标识
  * @param strategyId 系统策略编号
  * @param orderNo  系统内部订单编号
  * @param localID 本地委托号  （形如：策略编号-订单编号）
  * @param mdDate 最新行情对应的时间(回报返回时行情里的时间可能不是当前时间)
  * @param accountID 资金账户ID(可能为空，因为仅是为了开发跟随策略追加的，在除成交回报外的回报对象中并没有设置此值)
  * @param direction 买卖方向(仅在做市应价单时使用，因为部分成交、全部成交是靠quant4s自己模拟，而不是从交易前置发出，
  *                  但在双边报价时，需要同时更新两个单子的状态，仅在成交回报返回时此值有效，其他状态如未成交还在队列中，则不需要此字段)
  * @param orderSysID 报单编号(由柜台生成，用于在柜台内部唯一标识这笔委托)
  */
abstract class OrderResult(id: String, strategyId: Int, orderNo: Int, localID: String, mdDate: Date, accountID: Option[String], direction: Int, orderSysID: String) {

}

/**
  * 订单状态结果，主要用于传递经纪系统响应的订单状态
  *
  * @param id 唯一标识
  * @param strategyId 系统策略编号
  * @param orderNo  系统内部订单编号
  * @param localID 本地委托号  （形如：策略编号-订单编号）
  * @param mdDate 最新行情对应的时间(回报返回时行情里的时间可能不是当前时间)
  * @param accountID 资金账户ID(可能为空，因为仅是为了开发跟随策略追加的，在除成交回报外的回报对象中并没有设置此值)
  * @param direction 买卖方向(仅在做市应价单时使用，因为部分成交、全部成交是靠quant4s自己模拟，而不是从交易前置发出，
  *                  但在双边报价时，需要同时更新两个单子的状态，仅在成交回报返回时此值有效，其他状态如未成交还在队列中，则不需要此字段)
  * @param orderSysID 报单编号(由柜台生成，用于在柜台内部唯一标识这笔委托)
  * @param orderStatus 订单状态  0：待报，1：已报 2：废单  3：撤单 4：部分成交 5：全部成交
  */
case class OrderStatusResult(id: String, strategyId: Int, orderNo: Int, localID: String, mdDate: Date, accountID: Option[String], direction: Int, orderSysID: String, orderStatus: Int)
  extends  OrderResult(id, strategyId, orderNo, localID, mdDate, accountID, direction, orderSysID)

/**
  * 订单处理结果记录
 *
  * @param id 唯一标识
  * @param strategyId 系统策略编号
  * @param orderNo  系统内部订单编号
  * @param localID 本地委托号  （形如：策略编号-订单编号）
  * @param mdDate 最新行情对应的时间(回报返回时行情里的时间可能不是当前时间)
  * @param accountID 资金账户ID(可能为空，因为仅是为了开发跟随策略追加的，在除成交回报外的回报对象中并没有设置此值)
  * @param contractCode 证券代码 | 合约代码
  * @param direction 买卖方向(仅在做市应价单时使用，因为部分成交、全部成交是靠quant4s自己模拟，而不是从交易前置发出，
  *                  但在双边报价时，需要同时更新两个单子的状态，仅在成交回报返回时此值有效，其他状态如未成交还在队列中，则不需要此字段)
  * @param OCType
  * @param dealID 成交编号
  * @param dealPrice 成交价格
  * @param dealVol  成交数量
  * @param orderSysID 报单编号(由柜台生成，用于在柜台内部唯一标识这笔委托)
  */
case class OrderDealResult(id: String, strategyId: Int, orderNo: Int, localID: String, mdDate: Date, accountID: Option[String], contractCode: String,
                           direction: Int, OCType: Int, dealID: String, dealPrice: Double, dealVol: Int, orderSysID: String)
  extends OrderResult(id, strategyId, orderNo, localID,mdDate, accountID, direction, orderSysID)

case class OrderCancelResult(id: String, strategyId: Int, orderNo: Int, localID: String, mdDate: Date, accountID: Option[String], contractCode: String,
                             direction: Int, OCType: Int, dealID: String, dealPrice: Double, dealVol: Int, orderSysID: String, orderStatus: Int)
  extends OrderResult(id, strategyId, orderNo, localID,mdDate, accountID, direction, orderSysID)
