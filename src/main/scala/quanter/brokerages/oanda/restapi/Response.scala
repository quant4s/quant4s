/**
  *
  */
package quanter.brokerages.oanda.restapi

import java.time.ZonedDateTime

import quanter.brokerages.oanda.common.model.TransactionJsonProtocol._
import quanter.brokerages.oanda.common.model.{Side, Transaction}
import quanter.brokerages.oanda.common.util.DateUtils._
import quanter.brokerages.oanda.restapi.model.Candle.CandleJsonProtocol._
import quanter.brokerages.oanda.restapi.model.Candle.{BidAskBasedCandle, MidPointBasedCandle}
import quanter.brokerages.oanda.restapi.model.{Candle, Granularity, OrderType}
import spray.json.DefaultJsonProtocol

trait Response

object Response {

  case class Instrument(instrument: String,
                        displayName: Option[String],
                        pip: Option[String],
                        precision: Option[String],
                        maxTradeUnits: Option[Double],
                        maxTrailingStop: Option[Double],
                        minTrailingStop: Option[Double],
                        marginRate: Option[Double],
                        halted: Option[Boolean])

  case class GetInstrumentsResponse(instruments: Seq[Instrument]) extends Response

  case class Price(instrument: String,
                   time: ZonedDateTime,
                   bid: Double,
                   ask: Double,
                   status: Option[String])

  case class GetCurrentPricesResponse(prices: Seq[Price]) extends Response

  case class CandleResponse[+T <: Candle](instrument: String, granularity: Granularity, candles: Seq[T]) extends Response

  case class Account(accountId: Int, accountName: String, accountCurrency: String, marginRate: Double)

  case class GetAccountsResponse(accounts: Seq[Account]) extends Response

  case class CreateTestAccountResponse(username: String, password: String, accountId: Int) extends Response

  case class GetAccountInformationResponse(accountId: Int,
                                           accountName: String,
                                           balance: BigDecimal,
                                           unrealizedPl: BigDecimal,
                                           realizedPl: BigDecimal,
                                           marginUsed: BigDecimal,
                                           marginAvail: BigDecimal,
                                           openTrades: Int,
                                           openOrders: Int,
                                           marginRate: Double,
                                           accountCurrency: String) extends Response

  case class OrderOpened(id: Long,
                         units: Long,
                         side: Side,
                         expiry: ZonedDateTime,
                         upperBound: Double,
                         lowerBound: Double,
                         takeProfit: Double,
                         stopLoss: Double,
                         trailingStop: Double)

  case class TradeOpened(id: Long, units: Long, side: Side, takeProfit: Double, stopLoss: Double, trailingStop: Double)

  case class OrderResponse(id: Long,
                           instrument: String,
                           units: Long,
                           side: Side,
                           `type`: OrderType,
                           time: ZonedDateTime,
                           price: Double,
                           takeProfit: Double,
                           stopLoss: Double,
                           expiry: ZonedDateTime,
                           upperBound: Double,
                           lowerBound: Double,
                           trailingStop: Double) extends Response

  case class GetOrdersResponse(orders: Seq[OrderResponse]) extends Response

  case class CreateOrderResponse(instrument: String, time: ZonedDateTime, price: Double, orderOpened: Option[OrderOpened], tradeOpened: Option[TradeOpened]) extends Response

  case class CloseOrderResponse(id: Long, instrument: String, units: Long, side: Side, price: Double, time: ZonedDateTime) extends Response

  case class TradeResponse(id: Long,
                           units: Long,
                           side: Side,
                           instrument: String,
                           time: ZonedDateTime,
                           price: Double,
                           takeProfit: Double,
                           stopLoss: Double,
                           trailingStop: Double,
                           trailingAmount: Double) extends Response

  case class GetOpenTradesResponse(trades: Seq[TradeResponse]) extends Response

  case class CloseTradeResponse(id: Long,
                                price: Double,
                                instrument: String,
                                profit: Double,
                                side: Side,
                                time: ZonedDateTime) extends Response

  case class PositionResponse(instrument: String, units: Long, side: Side, avgPrice: Double) extends Response

  case class GetOpenPositionsResponse(positions: Seq[PositionResponse]) extends Response

  case class ClosePositionResponse(ids: Seq[Long], instrument: String, totalUnits: Long, price: Double) extends Response

  case class GetTransactionHistoryResponse(transactions: Seq[Transaction]) extends Response

  object ResponseJsonProtocol extends DefaultJsonProtocol {
    implicit val closeOrderResponseFormat = jsonFormat6(CloseOrderResponse)
    implicit val orderOpenedFormat = jsonFormat9(OrderOpened)
    implicit val tradeOpenedFormat = jsonFormat6(TradeOpened)
    implicit val createOrderResponseFormat = jsonFormat5(CreateOrderResponse)
    implicit val tradeResponseFmt = jsonFormat10(TradeResponse)
    implicit val getOpenTradesResponseFmt = jsonFormat1(GetOpenTradesResponse)
    implicit val orderResponseFormat = jsonFormat13(OrderResponse)
    implicit val getOrdersResponseFmt = jsonFormat1(GetOrdersResponse)
    implicit val midPointBasedCandleResponseFmt = jsonFormat3(CandleResponse[MidPointBasedCandle])
    implicit val bidAskBasedCandleResponseFmt = jsonFormat3(CandleResponse[BidAskBasedCandle])
    implicit val closePositionResponseFmt = jsonFormat4(ClosePositionResponse)
    implicit val closeTradeResponseFmt = jsonFormat6(CloseTradeResponse)
    implicit val instrumentFmt = jsonFormat9(Instrument)
    implicit val getInstrumentsResponseFmt = jsonFormat1(GetInstrumentsResponse)
    implicit val priceFmt = jsonFormat5(Price)
    implicit val getCurrentPricesResponseFmt = jsonFormat1(GetCurrentPricesResponse)
    implicit val accountFmt = jsonFormat4(Account)
    implicit val getAccountsResponseFmt = jsonFormat1(GetAccountsResponse)
    implicit val createTestAccountResponseFmt = jsonFormat3(CreateTestAccountResponse)
    implicit val getAccountInformationResponseFmt = jsonFormat11(GetAccountInformationResponse)
    implicit val positionResponseFmt = jsonFormat4(PositionResponse)
    implicit val getOpenPositionsResponseFmt = jsonFormat1(GetOpenPositionsResponse)
    implicit val getTransactionHistoryResponseFmt = jsonFormat1(GetTransactionHistoryResponse)
  }

}