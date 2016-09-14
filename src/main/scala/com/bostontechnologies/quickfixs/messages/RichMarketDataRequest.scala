package com.bostontechnologies.quickfixs.messages

import quickfix.field._
import com.bostontechnologies.quickfixs.components.Instrument
import scala.collection.JavaConversions._
import quickfix.{Group, Message}
import quickfix.fix50.MarketDataRequest

class RichMarketDataRequest private(self: Message) extends RichMessage(self) {

	require(RichMessage.isA(self, RichMarketDataRequest.msgType))

	def hasRequestId: Boolean = self.isSetField(MDReqID.FIELD)

	def requestId: String = self.getString(MDReqID.FIELD)

	def requestId_=(id: String) {
		self.setString(MDReqID.FIELD, id)
	}

	def hasRequestType: Boolean = self.isSetField(SubscriptionRequestType.FIELD)

	def requestType: Char = self.getChar(SubscriptionRequestType.FIELD)

	def requestType_=(value: Char) {
		self.setChar(SubscriptionRequestType.FIELD, value)
	}

	def hasMarketDepth: Boolean = self.isSetField(MarketDepth.FIELD)

	def marketDepth: Int = self.getInt(MarketDepth.FIELD)

	def marketDepth_=(value: Int) {
		self.setInt(MarketDepth.FIELD, value)
	}

	def hasUpdateType: Boolean = self.isSetField(MDUpdateType.FIELD)

	def updateType: Int = self.getInt(MDUpdateType.FIELD)

	def updateType_=(value: Int) {
		self.setInt(MDUpdateType.FIELD, value)
	}

	def instrumentCount: Int = self.getGroupCount(NoRelatedSym.FIELD)

	def instruments: List[Instrument] = {
		self.getGroups(NoRelatedSym.FIELD).map(Instrument(_)).toList
	}

	def +=(inst: Instrument) {
		val group = new Group(NoRelatedSym.FIELD, Symbol.FIELD)
		group.setFields(inst.toFields)
		self.addGroup(group)
	}

	def entryTypeCount: Int = self.getGroupCount(NoMDEntryTypes.FIELD)

	def entryTypes: List[Char] =
		self.getGroups(NoMDEntryTypes.FIELD).map(_.getChar(MDEntryType.FIELD)).toList

	def +=(entryType: Char) {
		val group = new Group(NoMDEntryTypes.FIELD, MDEntryType.FIELD)
		group.setChar(MDEntryType.FIELD, entryType)
		self.addGroup(group)
	}

	def +=(entryTypes: Iterable[Char]) {
		entryTypes.foreach(this += _)
	}
}

object RichMarketDataRequest extends RichMessageExtractor[RichMarketDataRequest, MarketDataRequest] {

  val msgType = MsgType.MARKET_DATA_REQUEST

	def apply(message: quickfix.fix50.MarketDataRequest): RichMarketDataRequest =
		new RichMarketDataRequest(message)

	def new50Message: RichMarketDataRequest = this(new quickfix.fix50.MarketDataRequest)

	def apply(message: quickfix.fix44.MarketDataRequest): RichMarketDataRequest =
		new RichMarketDataRequest(message)

	def new44Message: RichMarketDataRequest = this(new quickfix.fix44.MarketDataRequest)

  def apply(message: quickfix.fix42.MarketDataRequest): RichMarketDataRequest =
  		new RichMarketDataRequest(message)

  def newMessage: RichMarketDataRequest = new RichMarketDataRequest(
    RichMessage.newMessage(msgType).self)

}





