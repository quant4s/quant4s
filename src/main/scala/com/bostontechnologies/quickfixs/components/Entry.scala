package com.bostontechnologies.quickfixs.components

import quickfix.field._
import java.util.Date
import java.math.BigDecimal
import com.bostontechnologies.quickfixs.fields.RichGroup
import quickfix.fix50.Message
import quickfix.Group
import com.bostontechnologies.quickfixs.TaggedType._
import com.bostontechnologies.quickfixs.{Fix44, Fix50, FixVersion}

class Entry(val self: Group) extends RichGroup {

  def entryType: Char = self.getChar(MDEntryType.FIELD)

  def entryType_=(value: Char) {
    self.setChar(MDEntryType.FIELD, value)
  }

  def timestamp: Date = {
    if (self.isSetField(MDEntryDate.FIELD) && self.isSetField(MDEntryTime.FIELD))
      new Date(self.getUtcDateOnly(MDEntryDate.FIELD).getTime + self.getUtcTimeOnly(MDEntryTime.FIELD).getTime)
    else new Date
  }

  def timestamp_=(value: Date) {
    self.setUtcDateOnly(MDEntryDate.FIELD, value)
    self.setUtcTimeOnly(MDEntryTime.FIELD, value, true)
  }

  def price: Option[BigDecimal] = getDecimal(MDEntryPx.FIELD)

  def price_=(value: BigDecimal) {
    self.setDecimal(MDEntryPx.FIELD, value)
  }

  def price_=(value: String) {
    price_=(new BigDecimal(value))
  }

  def clearPrice() {
    self.removeField(MDEntryPx.FIELD)
  }

  def size: Option[BigDecimal] = getDecimal(MDEntrySize.FIELD)

  def size_=(value: BigDecimal) {
    self.setDecimal(MDEntrySize.FIELD, value)
  }

  def size_=(value: String) {
    size_=(new BigDecimal(value))
  }

  def clearSize() {
    self.removeField(MDEntrySize.FIELD)
  }

  def entryId: Option[String] = getString(MDEntryID.FIELD)

  def entryId_=(value: String) {
    self.setString(MDEntryID.FIELD, value)
  }

  def clearEntryId() {
    self.removeField(MDEntryID.FIELD)
  }

  def quoteEntryId: Option[String] = getString(QuoteEntryID.FIELD)
  
  def quoteEntryId_=(value: String) {
    self.setString(QuoteEntryID.FIELD, value)
  }
  
  def clearQuoteEntryId() {
    self.removeField(QuoteEntryID.FIELD)
  }

  def hasCurrency: Boolean = self.isSetField(Currency.FIELD)

  def currency: Option[String] = getString(Currency.FIELD)

  def currency_=(value: String) {
    self.setString(Currency.FIELD, value)
  }

  def clearCurrency() {
    self.removeField(Currency.FIELD)
  }

  def toFields: Group = self

  def getDecimal(field: Int): Option[BigDecimal] = if (self.isSetField(field))
    Some(self.getDecimal(field))
  else
    None

  def getString(field: Int): Option[String] = if (self.isSetField(field))
    Some(self.getString(field))
  else
    None

  def getChar(field: Int): Option[Char] = if (self.isSetField(field))
    Some(self.getChar(field))
  else
    None

}

object Entry {

  val GroupFieldNumber = NoMDEntries.FIELD
  val GroupDelimiter = MDEntryType.FIELD

  sealed trait FieldOrder

  implicit val fix44FieldOrder: Array[Int] @@ Fix44 = tag[Fix44](Array(MDEntryType.FIELD, 270, 15, 271, 272, 273, 274, 275, 336, 625, 276, 277, 282, 283, 284, 286, 59, 432, 126, 110, 18, 287, 37, 299, 288, 289, 346, 290, 546, 811, 58, 354, 355, 0))

  implicit val fix50FieldOrder: Array[Int] @@ Fix50 = tag[Fix50](Array(MDEntryType.FIELD, 278, 270, 15, 271, 272, 273, 274, 275, 336, 625, 276, 277, 282, 283, 284, 286, 59, 432, 126, 110, 18, 287, 37, 299, 288, 289, 346, 290, 546, 811, 58, 354, 355, 1023, 528, 1024, 332, 333, 1020, 63, 64, 1070, 83, 1048, 1026, 1027, 453, 198, 40, 0))

  implicit def entryBuilder[V <: FixVersion](implicit version: V, fieldOrder: Array[Int] @@ V): V => Entry =
    version => new Entry(newGroup[V](version, fieldOrder))

  implicit def newGroup[V <: FixVersion](implicit version: V, fieldOrder: Array[Int] @@ V) = new Group(GroupFieldNumber, GroupDelimiter, fieldOrder)

  def apply[T <: Entry, V <: FixVersion](entryType: Char,
                                         timestamp: Date,
                                         entryId: Option[String],
                                         price: Option[String],
                                         size: Option[String],
                                         quoteEntryId: Option[String],
                                         currency: Option[String])
                                        (implicit version: V, entryBuilder: V => T): T = {
    val entry = entryBuilder(version)
    require(timestamp != null)

    implicit val fields = new Message
    entry.entryType = entryType
    entry.timestamp = timestamp
    entryId.foreach(entry.entryId = _)
    price.foreach(entry.price = _)
    size.foreach(entry.size = _)
    quoteEntryId.foreach(entry.quoteEntryId = _)
    currency.foreach(entry.currency = _)
    entry
  }

  def apply[V <: FixVersion](entryType: Char,
                             timestamp: Date = new Date,
                             entryId  : Option[String] = None,
                             price: Option[String] = None,
                             size: Option[String] = None,
                             quoteEntryId: Option[String] = None,
                             currency: Option[String] = None): Entry =
    apply[Entry, Fix50](entryType = entryType,
      timestamp = timestamp,
      currency = currency,
      entryId = entryId,
      price = price,
      size = size,
      quoteEntryId = quoteEntryId)

  def apply(entryType: Char, entryId: String, price: String, size: String, quoteEntryId: String, currency: Option[String]): Entry =
    apply[Entry, Fix50](entryType, new Date, Some(entryId), Some(price), Some(size), Some(quoteEntryId), currency)

  def apply(group: Group): Entry = new Entry(group)

}
