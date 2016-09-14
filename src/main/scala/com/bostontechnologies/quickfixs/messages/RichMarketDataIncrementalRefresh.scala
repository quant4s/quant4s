package com.bostontechnologies.quickfixs.messages

import scala.collection.JavaConversions._
import quickfix.fix50.MarketDataIncrementalRefresh
import com.bostontechnologies.quickfixs.components.IncrementalEntry
import quickfix.Message
import quickfix.field.{MDEntryType, NoMDEntries, MDReqID, MsgType}

class RichMarketDataIncrementalRefresh private(self: Message) extends RichMessage(self) {
  require(RichMessage.isA(self, RichMarketDataIncrementalRefresh.msgType))

  def hasRequestId: Boolean = self.isSetField(MDReqID.FIELD)

  def requestId: String = self.getString(MDReqID.FIELD)

  def requestId_=(id: String) {
    self.setString(MDReqID.FIELD, id)
  }

  def incrementalEntryCount: Int = self.getInt(NoMDEntries.FIELD)

  def incrementalEntries: List[IncrementalEntry] = self.getGroups(NoMDEntries.FIELD).map(
    new IncrementalEntry(_) ).toList

  def +=(incrementalEntry: IncrementalEntry) {
    self.addGroup(incrementalEntry.toFields)
  }

  def ++=(entries: Iterable[IncrementalEntry]) {
    entries.foreach(this += _)
  }

  def bids = incrementalEntries.filter(e => e.entryType == MDEntryType.BID)

  def offers = incrementalEntries.filter(e => e.entryType == MDEntryType.OFFER)
}

object RichMarketDataIncrementalRefresh extends RichMessageExtractor[RichMarketDataIncrementalRefresh,
  MarketDataIncrementalRefresh] {
  
  val msgType = MsgType.MARKET_DATA_INCREMENTAL_REFRESH

  def apply(message: quickfix.fix50.MarketDataIncrementalRefresh): RichMarketDataIncrementalRefresh =
    new RichMarketDataIncrementalRefresh(message)

  def apply(message: quickfix.fix42.MarketDataIncrementalRefresh): RichMarketDataIncrementalRefresh =
  		new RichMarketDataIncrementalRefresh(message)
  
  def newMessage: RichMarketDataIncrementalRefresh = new RichMarketDataIncrementalRefresh(
    new quickfix.fix50.MarketDataIncrementalRefresh)
}