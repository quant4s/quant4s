package com.bostontechnologies.quickfixs.messages

import scala.collection.JavaConversions._
import quickfix.field._
import quickfix.Message
import com.bostontechnologies.quickfixs.components.{RoutingGroup, Entry}
import com.bostontechnologies.quickfixs.fields.RichGroup
import quickfix.fix50.MarketDataSnapshotFullRefresh

class RichMarketDataSnapshotFullRefresh private(self: Message) extends RichMessage(self) with InstrumentFields[Message] {

	require(RichMessage.isA(self, RichMarketDataSnapshotFullRefresh.msgType))

	def hasRequestId: Boolean = self.isSetField(MDReqID.FIELD)

	def requestId: String = self.getString(MDReqID.FIELD)

	def requestId_=(id: String) {
		self.setString(MDReqID.FIELD, id)
	}
	def entryCount: Int = self.getInt(NoMDEntries.FIELD)

	def entries: List[Entry] = self.getGroups(NoMDEntries.FIELD).map( Entry(_) ).toList

  def routingGroups: List[RoutingGroup] = self.getGroups(NoRoutingIDs.FIELD).map( RoutingGroup(_)).toList

	def +=(group: RichGroup) {
		self.addGroup(group.toFields)
  }

  def ++=(groups: Iterable[RichGroup]) {
    groups.foreach( this += _)
  }

}

object RichMarketDataSnapshotFullRefresh extends RichMessageExtractor[RichMarketDataSnapshotFullRefresh, MarketDataSnapshotFullRefresh] {

	val msgType = MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH

	def apply(message: quickfix.fix50.MarketDataSnapshotFullRefresh): RichMarketDataSnapshotFullRefresh =
		new RichMarketDataSnapshotFullRefresh(message)

	def new50Message: RichMarketDataSnapshotFullRefresh = this(new quickfix.fix50.MarketDataSnapshotFullRefresh)

	def apply(message: quickfix.fix44.MarketDataSnapshotFullRefresh): RichMarketDataSnapshotFullRefresh =
		new RichMarketDataSnapshotFullRefresh(message)

	def new44Message: RichMarketDataSnapshotFullRefresh = this(new quickfix.fix44.MarketDataSnapshotFullRefresh)

	def newMessage: RichMarketDataSnapshotFullRefresh = new RichMarketDataSnapshotFullRefresh(
    RichMessage.newMessage(msgType).self)
}
