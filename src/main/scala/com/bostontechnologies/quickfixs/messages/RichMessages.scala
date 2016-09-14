package com.bostontechnologies.quickfixs.messages

import quickfix.Message

trait RichMessages {

  implicit def enrichFixMessage(message: Message): RichMessage = new RichMessage(message)

	implicit def enrichExecutionReport50(message: quickfix.fix50.ExecutionReport): RichExecutionReport =
		RichExecutionReport(message)

	implicit def enrichExecutionReport44(message: quickfix.fix44.ExecutionReport): RichExecutionReport =
		RichExecutionReport(message)

	implicit def enrichMarketDataRequest50(message: quickfix.fix50.MarketDataRequest): RichMarketDataRequest =
		RichMarketDataRequest(message)

	implicit def enrichMarketDataRequest44(message: quickfix.fix44.MarketDataRequest): RichMarketDataRequest =
		RichMarketDataRequest(message)

	implicit def enrichMarketDataRequestReject50(message: quickfix.fix50.MarketDataRequestReject): RichMarketDataRequestReject =
		RichMarketDataRequestReject(message)

	implicit def enrichMarketDataRequestReject44(message: quickfix.fix44.MarketDataRequestReject): RichMarketDataRequestReject =
		RichMarketDataRequestReject(message)

	implicit def enrichMarketDataSnapshotFullRefresh50(message: quickfix.fix50.MarketDataSnapshotFullRefresh): RichMarketDataSnapshotFullRefresh =
		RichMarketDataSnapshotFullRefresh(message)

	implicit def enrichMarketDataSnapshotFullRefresh44(message: quickfix.fix44.MarketDataSnapshotFullRefresh): RichMarketDataSnapshotFullRefresh =
		RichMarketDataSnapshotFullRefresh(message)

	implicit def enrichNewOrderSingle50(message: quickfix.fix50.NewOrderSingle): RichNewOrderSingle =
		RichNewOrderSingle(message)

	implicit def enrichNewOrderSingle44(message: quickfix.fix44.NewOrderSingle): RichNewOrderSingle =
		RichNewOrderSingle(message)

	implicit def enrichOrderCancelRequest50(message: quickfix.fix50.OrderCancelRequest): RichOrderCancelRequest =
		RichOrderCancelRequest(message)

	implicit def enrichOrderCancelRequest44(message: quickfix.fix44.OrderCancelRequest): RichOrderCancelRequest =
		RichOrderCancelRequest(message)

  implicit def impoverishMessage(message: RichMessage): Message = message.self
}

object RichMessages extends RichMessages

