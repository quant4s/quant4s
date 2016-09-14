package com.bostontechnologies.quickfixs.components

import quickfix.{FieldNotFound, FieldMap, Message}
import quickfix.field.{SymbolSfx, Product, SecurityType, Symbol}

class Instrument {

  private var _symbol: Option[String] = None
  private var _product: Option[Int] = None
  private var _securityType: Option[String] = None
  private var _suffix: Option[String] = None

  def symbol: Option[String] = _symbol

  def symbol_=(value: String) {
    _symbol = Option(value)
  }

  def clearSymbol() {
    _symbol = None
  }

  def product: Option[Int] = _product

  def product_=(value: Int) {
    _product = Some(value)
  }

  def clearProduct() {
    _product = None
  }

  def securityType: Option[String] = _securityType

  def securityType_=(value: String) {
    _securityType = value match {
      case null => None
      case "" => None
      case _ => Some(value)
    }
  }

  def clearSecurityType() {
    _securityType = None
  }

  def suffix: Option[String] = _suffix

  def suffix_=(value: String) {
    _suffix = Some(value)
  }

  def clearSuffix() {
    _suffix = None
  }

  def toFields: FieldMap = {
    val fields = new Message

    if (symbol.isDefined) {
      fields.setString(Symbol.FIELD, symbol.get)
    }

    if (product.isDefined) {
      fields.setInt(Product.FIELD, product.get)
    }

    if (securityType.isDefined) {
      fields.setString(SecurityType.FIELD, securityType.get)
    }

    if (suffix.isDefined) {
      fields.setString(SymbolSfx.FIELD, suffix.get)
    }
    fields
  }
}

object Instrument {

  @throws(classOf[FieldNotFound])
  def apply(fields: FieldMap): Instrument = {
    val instrument = new Instrument()

    if (fields.isSetField(Symbol.FIELD)) {
      instrument.symbol = fields.getString(Symbol.FIELD)
    }

    if (fields.isSetField(Product.FIELD)) {
      instrument.product = fields.getInt(Product.FIELD)
    }

    if (fields.isSetField(SecurityType.FIELD)) {
      instrument.securityType = fields.getString(SecurityType.FIELD)
    }

    if (fields.isSetField(SymbolSfx.FIELD)) {
      instrument.suffix = fields.getString(SymbolSfx.FIELD)
    }

    instrument
  }

  def apply(other: Instrument): Instrument = {
    val instrument = new Instrument

    if (other.symbol.isDefined) {
      instrument.symbol = other.symbol.get
    }

    if (other.product.isDefined) {
      instrument.product = other.product.get
    }

    if (other.suffix.isDefined) {
      instrument.suffix = other.suffix.get
    }

    if (other.securityType.isDefined) {
      instrument.securityType = other.securityType.get
    }

    instrument
  }

  def apply(symbol: String, product: Int, suffix:String): Instrument = {
    val instrument = Instrument(symbol)
    instrument.product = product
    instrument.suffix = suffix

    instrument
  }

  def apply(symbol: String, product: Int): Instrument = {
    val instrument = Instrument(symbol)
    instrument.product = product

    instrument
  }

  def apply(symbol: String): Instrument = {
    val instrument = new Instrument
    instrument.symbol = symbol

    instrument
  }
}
