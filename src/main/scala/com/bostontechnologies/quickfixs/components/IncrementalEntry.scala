package com.bostontechnologies.quickfixs.components

import quickfix.field._
import java.util.Date
import com.bostontechnologies.quickfixs.messages.InstrumentFields
import com.bostontechnologies.quickfixs._
import quickfix.Group

class IncrementalEntry(self: Group) extends Entry(self) with InstrumentFields[Group] {

  def updateAction: Char = self.getChar(MDUpdateAction.FIELD)

  def updateAction_=(value: Char) {
    self.setChar(MDUpdateAction.FIELD, value)
  }

  override def toFields: Group = self
}

object IncrementalEntry {

  import TaggedType._

  val GroupFieldNumber = NoMDEntries.FIELD
  val GroupDelimiter = MDUpdateAction.FIELD

  implicit val fix50FieldOrder: Array[Int] @@ Fix50 = tag[Fix50](Array(MDUpdateAction.FIELD, 285, 269, 278, 280, 1500, 55, 65, 48, 22, 454, 460, 461, 167, 762, 200, 541, 224, 225, 239, 226, 227, 228, 255, 543, 470, 471, 472, 240, 202, 947, 206, 231, 223, 207, 106, 348, 349, 107, 350, 351, 691, 667, 875, 876, 864, 873, 874, 965, 966, 1049, 967, 968, 969, 970, 971, 1018, 996, 997, 1079, 711, 555, 291, 292, 270, 15, 271, 272, 273, 274, 275, 336, 625, 276, 277, 282, 283, 284, 286, 59, 432, 126, 110, 18, 287, 37, 299, 288, 289, 346, 290, 546, 811, 451, 58, 354, 355, 528, 1024, 332, 333, 1020, 63, 64, 1070, 83, 1048, 1026, 1027, 1023, 453, 198, 40, 0))

  implicit val fix44FieldOrder: Array[Int] @@ Fix44 = tag[Fix44](Array(MDUpdateAction.FIELD, 285, 269, 278, 280, 1500, 55, 65, 48, 22, 454, 460, 461, 167, 762, 200, 541, 224, 225, 239, 226, 227, 228, 255, 543, 470, 471, 472, 240, 202, 947, 206, 231, 223, 207, 106, 348, 349, 107, 350, 351, 691, 667, 875, 876, 864, 873, 874, 711, 555, 291, 292, 270, 15, 271, 272, 273, 274, 275, 336, 625, 276, 277, 282, 283, 284, 286, 59, 432, 126, 110, 18, 287, 37, 299, 288, 289, 346, 290, 546, 811, 451, 58, 354, 355, 0))

  implicit def entryBuilder[V <: FixVersion](implicit version: V, fieldOrder: Array[Int] @@ V): V => IncrementalEntry =
    version => new IncrementalEntry(newGroup[V](version, fieldOrder))

  def newGroup[V <: FixVersion](implicit version: V, fieldOrder: Array[Int] @@ V) = new Group(GroupFieldNumber, GroupDelimiter, fieldOrder)

  def apply[T <: IncrementalEntry, V <: FixVersion](updateAction: Char,
                                                    entryType: Char,
                                                    symbol: Option[String],
                                                    suffix: Option[String],
                                                    currency: Option[String],
                                                    timestamp: Date,
                                                    entryId: Option[String],
                                                    price: Option[String],
                                                    size: Option[String],
                                                    product: Option[Int],
                                                    quoteEntryId: Option[String])
                                                   (implicit version: V, incEntryBuilder: V => T): T = {
    require(updateAction != 0)

    val incEntry = Entry.apply[T, V](entryType, timestamp, entryId, price, size, quoteEntryId, currency)(version, incEntryBuilder)
    incEntry.updateAction = updateAction
    symbol.foreach(incEntry.symbol = _)
    suffix.foreach(incEntry.suffix = _)
    product.foreach(incEntry.product = _)
    incEntry
  }

  def apply[V <: FixVersion](updateAction: Char,
                             entryType: Char,
                             symbol: Option[String] = None,
                             suffix: Option[String] = None,
                             currency: Option[String] = None,
                             timestamp: Date = new Date,
                             entryId: Option[String] = None,
                             price: Option[String] = None,
                             size: Option[String] = None,
                             product: Option[Int] = None,
                             quoteEntryId: Option[String] = None)
                            (implicit version: V, fieldOrder: Array[Int] @@ V): IncrementalEntry =
    IncrementalEntry[IncrementalEntry, V](updateAction = updateAction,
      entryType = entryType,
      symbol = symbol,
      suffix = suffix,
      currency = currency,
      timestamp = timestamp,
      entryId = entryId,
      price = price,
      size = size,
      product = product,
      quoteEntryId = quoteEntryId)
}