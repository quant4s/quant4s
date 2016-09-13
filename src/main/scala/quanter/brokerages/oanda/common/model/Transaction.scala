/**
  *
  */
package quanter.brokerages.oanda.common.model

import java.time.ZonedDateTime


import quanter.brokerages.oanda.common.model.Transaction.Reason._
import quanter.brokerages.oanda.common.model.Transaction._
import quanter.brokerages.oanda.common.util.DateUtils._
import quanter.brokerages.oanda.restapi.Response

import spray.json._

sealed trait Transaction extends Response {
  def id: Int

  def accountId: Int

  def time: ZonedDateTime
}

object Transaction {

  sealed trait Reason

  object Reason {

    case object ClientRequest extends Reason

    case object Migration extends Reason

    case object ReplacesOrder extends Reason

    case object TimeInForceExpired extends Reason

    case object OrderFilled extends Reason

    case object InsufficientMargin extends Reason

    case object BoundsViolation extends Reason

    case object UnitsViolation extends Reason

    case object StopLossViolation extends Reason

    case object TakeProfitViolation extends Reason

    case object TrailingStopViolation extends Reason

    case object MarketHalted extends Reason

    case object AccountNonTradable extends Reason

    case object NoNewPositionAllowed extends Reason

    case object InsufficientLiquidity extends Reason

    case object Adjustment extends Reason

  }

  case class TradeOpened(id: Int, units: Int)

  case class TradeReduced(id: Int, units: Int, pl: Double, interest: Double)

  case class MarketOrderCreate(id: Int,
                               accountId: Int,
                               time: ZonedDateTime,
                               `type`: String,
                               instrument: String,
                               side: Side,
                               units: Int,
                               price: Double,
                               pl: Double,
                               interest: Double,
                               accountBalance: BigDecimal,
                               lowerBound: Option[Double],
                               upperBound: Option[Double],
                               takeProfitPrice: Option[Double],
                               stopLossPrice: Option[Double],
                               trailingStopLossDistance: Option[Double],
                               tradeOpened: Option[TradeOpened],
                               tradeReduced: Option[TradeReduced]) extends Transaction

  case class StopOrderCreate(id: Int,
                             accountId: Int,
                             time: ZonedDateTime,
                             `type`: String,
                             instrument: String,
                             side: Side,
                             units: Int,
                             price: Double,
                             expiry: ZonedDateTime,
                             reason: Reason,
                             lowerBound: Option[Double],
                             upperBound: Option[Double],
                             takeProfitPrice: Option[Double],
                             stopLossPrice: Option[Double],
                             trailingStopLossDistance: Option[Double]) extends Transaction

  case class LimitOrderCreate(id: Int,
                              accountId: Int,
                              time: ZonedDateTime,
                              `type`: String,
                              instrument: String,
                              side: Side,
                              units: Int,
                              price: Double,
                              expiry: ZonedDateTime,
                              reason: Reason,
                              lowerBound: Option[Double],
                              upperBound: Option[Double],
                              takeProfitPrice: Option[Double],
                              stopLossPrice: Option[Double],
                              trailingStopLossDistance: Option[Double]) extends Transaction

  case class MarketIfTouchedOrderCreate(id: Int,
                                        accountId: Int,
                                        time: ZonedDateTime,
                                        `type`: String,
                                        instrument: String,
                                        side: Side,
                                        units: Int,
                                        price: Double,
                                        expiry: ZonedDateTime,
                                        reason: Reason,
                                        lowerBound: Option[Double],
                                        upperBound: Option[Double],
                                        takeProfitPrice: Option[Double],
                                        stopLossPrice: Option[Double],
                                        trailingStopLossDistance: Option[Double]) extends Transaction

  case class OrderUpdate(id: Int,
                         accountId: Int,
                         time: ZonedDateTime,
                         `type`: String,
                         instrument: String,
                         units: Int,
                         price: Double,
                         reason: Reason,
                         lowerBound: Option[Double],
                         upperBound: Option[Double],
                         takeProfitPrice: Option[Double],
                         stopLossPrice: Option[Double],
                         trailingStopLossDistance: Option[Double]) extends Transaction

  case class OrderCancel(id: Int,
                         accountId: Int,
                         time: ZonedDateTime,
                         `type`: String,
                         orderId: Int,
                         reason: Reason) extends Transaction

  case class OrderFilled(id: Int,
                         accountId: Int,
                         time: ZonedDateTime,
                         `type`: String,
                         instrument: String,
                         units: Int,
                         side: Side,
                         price: Double,
                         pl: Double,
                         interest: Double,
                         accountBalance: BigDecimal,
                         orderId: Int,
                         lowerBound: Option[Double],
                         upperBound: Option[Double],
                         takeProfitPrice: Option[Double],
                         stopLossPrice: Option[Double],
                         trailingStopLossDistance: Option[Double],
                         tradeOpened: Option[TradeOpened],
                         tradeReduced: Option[TradeReduced]) extends Transaction

  case class TradeUpdate(id: Int,
                         accountId: Int,
                         time: ZonedDateTime,
                         `type`: String,
                         instrument: String,
                         units: Int,
                         tradeId: Int,
                         takeProfitPrice: Option[Double],
                         stopLossPrice: Option[Double],
                         trailingStopLossDistance: Option[Double]) extends Transaction

  case class TradeClose(id: Int,
                        accountId: Int,
                        time: ZonedDateTime,
                        `type`: String,
                        instrument: String,
                        units: Int,
                        side: Side,
                        price: Double,
                        pl: Double,
                        interest: Double,
                        accountBalance: BigDecimal,
                        tradeId: Int) extends Transaction

  case class MigrateTradeClose(id: Int,
                               accountId: Int,
                               time: ZonedDateTime,
                               `type`: String,
                               instrument: String,
                               units: Int,
                               side: Side,
                               price: Double,
                               pl: Double,
                               interest: Double,
                               accountBalance: BigDecimal,
                               tradeId: Int) extends Transaction

  case class MigrateTradeOpen(id: Int,
                              accountId: Int,
                              time: ZonedDateTime,
                              `type`: String,
                              instrument: String,
                              side: Side,
                              units: Int,
                              price: Double,
                              takeProfitPrice: Option[Double],
                              stopLossPrice: Option[Double],
                              trailingStopLossDistance: Option[Double],
                              tradeOpened: Option[TradeOpened]) extends Transaction

  case class TakeProfitFilled(id: Int,
                              accountId: Int,
                              time: ZonedDateTime,
                              `type`: String,
                              tradeId: Int,
                              instrument: String,
                              units: Int,
                              side: Side,
                              price: Double,
                              pl: Double,
                              interest: Double,
                              accountBalance: BigDecimal) extends Transaction

  case class StopLossFilled(id: Int,
                            accountId: Int,
                            time: ZonedDateTime,
                            `type`: String,
                            tradeId: Int,
                            instrument: String,
                            units: Int,
                            side: Side,
                            price: Double,
                            pl: Double,
                            interest: Double,
                            accountBalance: BigDecimal) extends Transaction

  case class TrailingStopFilled(id: Int,
                                accountId: Int,
                                time: ZonedDateTime,
                                `type`: String,
                                tradeId: Int,
                                instrument: String,
                                units: Int,
                                side: Side,
                                price: Double,
                                pl: Double,
                                interest: Double,
                                accountBalance: BigDecimal) extends Transaction

  case class MarginCallEnter(id: Int,
                             accountId: Int,
                             time: ZonedDateTime,
                             `type`: String) extends Transaction

  case class MarginCallExit(id: Int,
                            accountId: Int,
                            time: ZonedDateTime,
                            `type`: String) extends Transaction

  case class MarginCloseout(id: Int,
                            accountId: Int,
                            time: ZonedDateTime,
                            `type`: String,
                            instrument: String,
                            units: Int,
                            side: Side,
                            price: Double,
                            pl: Double,
                            interest: Double,
                            accountBalance: BigDecimal,
                            tradeId: Int) extends Transaction

  case class SetMarginRate(id: Int,
                           accountId: Int,
                           time: ZonedDateTime,
                           `type`: String,
                           marginRate: Double) extends Transaction

  case class TransferFunds(id: Int,
                           accountId: Int,
                           time: ZonedDateTime,
                           `type`: String,
                           amount: BigDecimal,
                           accountBalance: BigDecimal,
                           reason: Reason) extends Transaction

  case class DailyInterest(id: Int,
                           accountId: Int,
                           time: ZonedDateTime,
                           `type`: String,
                           instrument: String,
                           interest: Double,
                           accountBalance: BigDecimal) extends Transaction

  case class Fee(id: Int,
                 accountId: Int,
                 time: ZonedDateTime,
                 `type`: String,
                 amount: BigDecimal,
                 accountBalance: BigDecimal,
                 reason: Reason) extends Transaction

}


object TransactionJsonProtocol extends DefaultJsonProtocol {

  object ReasonJsonReader extends RootJsonReader[Reason] {
    override def read(value: JsValue): Reason = value match {
      case JsString(x) => x match {
        case "CLIENT_REQUEST" => ClientRequest
        case "MIGRATION" => Migration
        case "REPLACES_ORDER" => ReplacesOrder
        case "TIME_IN_FORCE_EXPIRED" => TimeInForceExpired
        case "ORDER_FILLED" => Reason.OrderFilled
        case "INSUFFICIENT_MARGIN" => InsufficientMargin
        case "BOUNDS_VIOLATION" => BoundsViolation
        case "UNITS_VIOLATION" => UnitsViolation
        case "STOP_LOSS_VIOLATION" => StopLossViolation
        case "TAKE_PROFIT_VIOLATION" => TakeProfitViolation
        case "TRAILING_STOP_VIOLATION" => TrailingStopViolation
        case "MARKET_HALTED" => MarketHalted
        case "ACCOUNT_NON_TRADABLE" => AccountNonTradable
        case "NO_NEW_POSITION_ALLOWED" => NoNewPositionAllowed
        case "INSUFFICIENT_LIQUIDITY" => InsufficientLiquidity
        case "ADJUSTMENT" => Adjustment
        case other => deserializationError("Reason not recognized: " + other)
      }
      case x => deserializationError("Expected 'reason' as JsString, but got " + x)
    }
  }

  implicit val reasonFmt = lift(ReasonJsonReader)

  implicit val tradeOpenedFmt = jsonFormat2(TradeOpened)
  implicit val tradeReducedFmt = jsonFormat4(TradeReduced)

  implicit val marketOrderCreateFmt = jsonFormat18(MarketOrderCreate)
  implicit val stopOrderCreateFmt = jsonFormat15(StopOrderCreate)
  implicit val limitOrderCreateFmt = jsonFormat15(LimitOrderCreate)
  implicit val marketIfTouchedOrderCreateFmt = jsonFormat15(MarketIfTouchedOrderCreate)
  implicit val orderUpdateFmt = jsonFormat13(OrderUpdate)
  implicit val orderCancelFmt = jsonFormat6(OrderCancel)
  implicit val orderFilledFmt = jsonFormat19(Transaction.OrderFilled)
  implicit val tradeUpdateFmt = jsonFormat10(TradeUpdate)
  implicit val tradeCloseFmt = jsonFormat12(TradeClose)
  implicit val migrateTradeCloseFmt = jsonFormat12(MigrateTradeClose)
  implicit val migrateTradeOpenFmt = jsonFormat12(MigrateTradeOpen)
  implicit val takeProfitFilledFmt = jsonFormat12(TakeProfitFilled)
  implicit val stopLossFilledFmt = jsonFormat12(StopLossFilled)
  implicit val trailingStopFilledFmt = jsonFormat12(TrailingStopFilled)
  implicit val marginCallEnterFmt = jsonFormat4(MarginCallEnter)
  implicit val marginCallExitFmt = jsonFormat4(MarginCallExit)
  implicit val marginCloseoutFmt = jsonFormat12(MarginCloseout)
  implicit val setMarginRateFmt = jsonFormat5(SetMarginRate)
  implicit val transferFundsFmt = jsonFormat7(TransferFunds)
  implicit val dailyInterestFmt = jsonFormat7(DailyInterest)
  implicit val feeFmt = jsonFormat7(Fee)

  object TransactionJsonReader extends RootJsonReader[Transaction] {
    def read(value: JsValue): Transaction = value.asJsObject.getFields("type") match {
      case Seq(JsString("MARKET_ORDER_CREATE")) => value.convertTo[MarketOrderCreate]
      case Seq(JsString("STOP_ORDER_CREATE")) => value.convertTo[StopOrderCreate]
      case Seq(JsString("LIMIT_ORDER_CREATE")) => value.convertTo[LimitOrderCreate]
      case Seq(JsString("MARKET_IF_TOUCHED_ORDER_CREATE")) => value.convertTo[MarketIfTouchedOrderCreate]
      case Seq(JsString("ORDER_UPDATE")) => value.convertTo[OrderUpdate]
      case Seq(JsString("ORDER_CANCEL")) => value.convertTo[OrderCancel]
      case Seq(JsString("ORDER_FILLED")) => value.convertTo[OrderFilled]
      case Seq(JsString("TRADE_UPDATE")) => value.convertTo[TradeUpdate]
      case Seq(JsString("TRADE_CLOSE")) => value.convertTo[TradeClose]
      case Seq(JsString("MIGRATE_TRADE_CLOSE")) => value.convertTo[MigrateTradeClose]
      case Seq(JsString("MIGRATE_TRADE_OPEN")) => value.convertTo[MigrateTradeOpen]
      case Seq(JsString("TAKE_PROFIT_FILLED")) => value.convertTo[TakeProfitFilled]
      case Seq(JsString("STOP_LOSS_FILLED")) => value.convertTo[StopLossFilled]
      case Seq(JsString("TRAILING_STOP_FILLED")) => value.convertTo[TrailingStopFilled]
      case Seq(JsString("MARGIN_CALL_ENTER")) => value.convertTo[MarginCallEnter]
      case Seq(JsString("MARGIN_CALL_EXIT")) => value.convertTo[MarginCallExit]
      case Seq(JsString("MARGIN_CLOSEOUT")) => value.convertTo[MarginCloseout]
      case Seq(JsString("SET_MARGIN_RATE")) => value.convertTo[SetMarginRate]
      case Seq(JsString("TRANSFER_FUNDS")) => value.convertTo[TransferFunds]
      case Seq(JsString("DAILY_INTEREST")) => value.convertTo[DailyInterest]
      case Seq(JsString("FEE")) => value.convertTo[Fee]
      case x => deserializationError("Transaction type is not recognized: " + x)
    }
  }

  implicit val transactionFmt = lift(TransactionJsonReader)
}