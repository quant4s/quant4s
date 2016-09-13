/**
  *
  */
package quanter.brokerages.oanda.restapi

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import quanter.brokerages.oanda.common.Environment
import quanter.brokerages.oanda.common.Environment.SandBox
import quanter.brokerages.oanda.common.model._
import quanter.brokerages.oanda.common.model.TransactionJsonProtocol._
import quanter.brokerages.oanda.common.util.DateUtils._
import quanter.brokerages.oanda.common.util.NumberUtils._
import quanter.brokerages.oanda.restapi.Request._
import quanter.brokerages.oanda.restapi.Response.ResponseJsonProtocol._
import quanter.brokerages.oanda.restapi.Response._
import quanter.brokerages.oanda.restapi.model.{Candle, CandleFormat}
import quanter.brokerages.oanda.restapi.model.Candle.{BidAskBasedCandle, MidPointBasedCandle}
import quanter.brokerages.oanda.restapi.model.CandleFormat.MidPoint
import spray.http.Uri
import spray.http.Uri.Query
import spray.can.Http
import spray.client.pipelining._
import spray.http.Uri.Query
import spray.http._
import spray.httpx.SprayJsonSupport._
import spray.httpx.unmarshalling._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object RestConnector {

  def props(env: Environment = SandBox, authToken: Option[String] = None, accountId: Int) = Props(new RestConnector(env, authToken, accountId))

}

class RestConnector(env: Environment, authTokenOpt: Option[String], accountId: Int) extends Actor with ActorLogging {

  import context._

  implicit val timeout = Timeout(5.seconds)

  private[this] def pipelineFuture[T <: Response](implicit unmarshaller: FromResponseUnmarshaller[T]): Future[HttpRequest => Future[T]] =
    for {
      Http.HostConnectorInfo(connector, _) <- IO(Http) ? Http.HostConnectorSetup(
        host = env.restApiUrl(),
        port = if (env.authenticationRequired()) 443 else 80,
        sslEncryption = env.authenticationRequired(),
        defaultHeaders = authTokenOpt.map(authToken => List(HttpHeaders.Authorization(OAuth2BearerToken(authToken)))).getOrElse(Nil)
      )
    } yield authTokenOpt match {
      case Some(authToken) => addCredentials(OAuth2BearerToken(authToken)) ~> sendReceive(connector) ~> unmarshal[T]
      case None => sendReceive(connector) ~> unmarshal[T]
    }

  private[this] def handleRequest[T <: Response](f: Future[T]) = {
    val requester = sender()
    f onComplete {
      case Success(response) =>
        log.debug("Received response from Oanda REST API: {}", response)
        requester ! response
      case Failure(t) =>
        log.error(t, "Error occurred while issuing HTTP request to Oanda REST API")
    }
  }

  override def receive = {

    case req: GetInstrumentsRequest =>
      log.info("Getting instruments: {}", req)
      val uri = Uri("/v1/instruments").withQuery(
        Query.asBodyData(
          Seq(
            Some(("accountId", accountId.toString)),
            req.fields.map(fields => ("fields", fields.mkString(","))),
            req.instruments.map(instruments => ("instruments", instruments.mkString(",")))
          ).flatten
        )
      )
      handleRequest(pipelineFuture[GetInstrumentsResponse].flatMap(_(Get(uri))))
    case req: GetCurrentPricesRequest =>
      log.info("Getting current prices: {}", req)
      val uri = Uri("/v1/prices").withQuery(
        Query.asBodyData(
          Seq(
            Some(("instruments", req.instruments.mkString(","))),
            req.since.map(since => ("since", dateTimeFormatter.format(since)))
          ).flatten
        )
      )
      handleRequest(pipelineFuture[GetCurrentPricesResponse].flatMap(_(Get(uri))))
    case req: GetCandlesRequest =>
      log.info("Getting historical candles: {}", req)
      val candlePipelineFuture: Future[HttpRequest => Future[CandleResponse[Candle]]] =
        if (req.candleFormat.contains(MidPoint))
          pipelineFuture[CandleResponse[MidPointBasedCandle]]
        else
          pipelineFuture[CandleResponse[BidAskBasedCandle]]
      val uri = Uri("/v1/candles").withQuery(
        Query.asBodyData(
          Seq(
            Some(("instrument", req.instrument)),
            req.granularity.map(g => ("granularity", g.toString)),
            req.count.map(c => ("count", c.toString)),
            req.start.map(s => ("start", dateTimeFormatter.format(s))),
            req.end.map(e => ("end", dateTimeFormatter.format(e))),
            req.candleFormat.map(c => ("candleFormat", c.toString)),
            req.includeFirst.map(i => ("includeFirst", i.toString)),
            req.dailyAlignment.map(d => ("dailyAlignment", d.toString)),
            req.alignmentTimeZone.map(a => ("alignmentTimeZone", a.toString)),
            req.weeklyAlignment.map(w => ("weeklyAlignment", w.toString))
          ).flatten
        )
      )
      handleRequest(candlePipelineFuture.flatMap(_(Get(uri))))

    case req: GetAccountsRequest =>
      log.info("Getting accounts: {}", req)
      val uri = Uri("/v1/accounts").withQuery(
        Query.asBodyData(
          Seq(req.username.map(u => ("username", u))).flatten
        )
      )
      handleRequest(pipelineFuture[GetAccountsResponse].flatMap(_(Get(uri))))
    case req: CreateTestAccountRequest =>
      log.info("Creating test account: {}", req)
      val data = FormData(req.currency.map(c => Seq(("currency", c))).getOrElse(Nil))
      handleRequest(pipelineFuture[CreateTestAccountResponse].flatMap(_(Post(s"/v1/accounts/", data))))
    case req: GetAccountInformationRequest =>
      log.info("Getting account information: {}", req)
      val uri = s"/v1/accounts/${req.accountId}"
      handleRequest(pipelineFuture[GetAccountInformationResponse].flatMap(_(Get(uri))))

    case req: GetOrdersRequest =>
      log.info("Getting open orders: {}", req)
      val uri = Uri(s"/v1/accounts/$accountId/orders").withQuery(
        Query.asBodyData(
          Seq(
            req.maxId.map(maxId => ("maxId", maxId.toString)),
            req.count.map(count => ("count", count.toString)),
            req.instrument.map(instrument => ("instrument", instrument)),
            req.ids.map(ids => ("ids", ids.mkString(",")))
          ).flatten
        )
      )
      handleRequest(pipelineFuture[GetOrdersResponse].flatMap(_(Get(uri))))
    case req: CreateOrderRequest =>
      log.info("Creating new order: {}", req)
      val data = FormData(
        Map(
          "instrument" -> req.instrument,
          "units" -> req.units.toString,
          "side" -> req.side.toString,
          "type" -> req.typ.toString
        ) ++ req.expiry.map(e => "expiry" -> dateTimeFormatter.format(e))
          ++ req.price.map("price" -> _.toString)
          ++ req.lowerBound.map("lowerBound" -> _.toString)
          ++ req.upperBound.map("upperBound" -> _.toString)
          ++ req.stopLoss.map("stopLoss" -> _.toString)
          ++ req.takeProfit.map("takeProfit" -> _.toString)
          ++ req.trailingStop.map("trailingStop" -> _.toString)
      )
      handleRequest(pipelineFuture[CreateOrderResponse].flatMap(_(Post(s"/v1/accounts/$accountId/orders", data))))
    case req: GetOrderInformationRequest =>
      log.info("Getting information for an order: {}", req)
      handleRequest(pipelineFuture[OrderResponse].flatMap(_(Get(s"/v1/accounts/$accountId/orders/${req.orderId}"))))
    case req: ModifyOrderRequest =>
      log.info("Modifying order: {}", req)
      val data = FormData(
        Map() ++ req.units.map("units" -> _.toString)
          ++ req.price.map("price" -> _.toString)
          ++ req.expiry.map(e => "expiry" -> dateTimeFormatter.format(e))
          ++ req.lowerBound.map("lowerBound" -> _.toString)
          ++ req.upperBound.map("upperBound" -> _.toString)
          ++ req.stopLoss.map("stopLoss" -> _.toString)
          ++ req.takeProfit.map("takeProfit" -> _.toString)
          ++ req.trailingStop.map("trailingStop" -> _.toString)
      )
      handleRequest(pipelineFuture[OrderResponse].flatMap(_(Patch(s"/v1/accounts/$accountId/orders/${req.id}", data))))
    case req: CloseOrderRequest =>
      log.info("Closing order {}", req)
      handleRequest(pipelineFuture[CloseOrderResponse].flatMap(_(Delete(s"/v1/accounts/$accountId/orders/${req.orderId}"))))

    case req: GetOpenTradesRequest =>
      log.info("Getting open trades: {}", req)
      val uri = Uri(s"/v1/accounts/$accountId/trades").withQuery(
        Query.asBodyData(
          Seq(
            req.maxId.map(maxId => ("maxId", maxId.toString)),
            req.count.map(count => ("count", count.toString)),
            req.instrument.map(instrument => ("instrument", instrument)),
            req.ids.map(ids => ("ids", ids.mkString(",")))
          ).flatten
        )
      )
      handleRequest(pipelineFuture[GetOpenTradesResponse].flatMap(_(Get(uri))))
    case req: GetTradeInformationRequest =>
      log.info("Getting information for trade: {}", req)
      handleRequest(pipelineFuture[TradeResponse].flatMap(_(Get(s"/v1/accounts/$accountId/trades/${req.tradeId}"))))
    case req: ModifyTradeRequest =>
      log.info("Modifying trade: {}", req)
      val data = FormData(
        Map() ++ req.takeProfit.map(tp => "takeProfit" -> decimalFormatter.format(tp))
          ++ req.stopLoss.map(sl => "stopLoss" -> decimalFormatter.format(sl))
          ++ req.trailingStop.map(ts => "trailingStop" -> decimalFormatter.format(ts))
      )
      handleRequest(pipelineFuture[TradeResponse].flatMap(_(Patch(s"/v1/accounts/$accountId/trades/${req.id}", data))))
    case req: CloseTradeRequest =>
      log.info("Closing trade: {}", req)
      handleRequest(pipelineFuture[CloseTradeResponse].flatMap(_(Delete(s"/v1/accounts/$accountId/trades/${req.tradeId}"))))

    case GetOpenPositionsRequest =>
      log.info("Getting open positions")
      handleRequest(pipelineFuture[GetOpenPositionsResponse].flatMap(_(Get(s"/v1/accounts/$accountId/positions"))))
    case req: GetPositionForInstrumentRequest =>
      log.info("Getting open positions for instrument: {}", req)
      handleRequest(pipelineFuture[PositionResponse].flatMap(_(Get(s"/v1/accounts/$accountId/positions/${req.instrument}"))))
    case req: ClosePositionRequest =>
      log.info("Closing position: {}", req)
      handleRequest(pipelineFuture[ClosePositionResponse].flatMap(_(Delete(s"/v1/accounts/$accountId/positions/${req.instrument}"))))

    case req: GetTransactionHistoryRequest =>
      log.info("Getting transaction history: {}", req)
      val uri = Uri(s"/v1/accounts/$accountId/transactions").withQuery(
        Query.asBodyData(
          Seq(
            req.maxId.map(maxId => ("maxId", maxId.toString)),
            req.minId.map(minId => ("minId", minId.toString)),
            req.count.map(count => ("count", count.toString)),
            req.instrument.map(instrument => ("instrument", instrument)),
            req.ids.map(ids => ("ids", ids.mkString(",")))
          ).flatten
        )
      )
      handleRequest(pipelineFuture[GetTransactionHistoryResponse].flatMap(_(Get(uri))))
    case req: GetTransactionInformationRequest =>
      log.info("Getting information for transaction: {}", req)
      handleRequest(pipelineFuture[Transaction].flatMap(_(Get(s"/v1/accounts/$accountId/transactions/${req.transactionId}"))))
  }
}