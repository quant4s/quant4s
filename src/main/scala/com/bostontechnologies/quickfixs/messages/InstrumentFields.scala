package com.bostontechnologies.quickfixs.messages

import com.bostontechnologies.quickfixs.components.Instrument
import quickfix.field.{SymbolSfx, SecurityType, Product, Symbol}
import quickfix.{FieldMap}

trait InstrumentFields[T <: FieldMap] extends RichFieldMap[T] {

	def hasSymbol: Boolean = self.isSetField(Symbol.FIELD)

	def symbol: String = self.getString(Symbol.FIELD)

	def symbol_=(symbol: String) {
		self.setString(Symbol.FIELD, symbol)
	}

  def hasSuffix: Boolean = self.isSetField(SymbolSfx.FIELD)

  def suffix: String = self.getString(SymbolSfx.FIELD)

  def suffix_=(suffix: String) {
		self.setString(SymbolSfx.FIELD, suffix)
	}

	def hasProduct: Boolean = self.isSetField(Product.FIELD)

	def product: Int = self.getInt(Product.FIELD)

	def product_=(product: Int) {
		self.setInt(Product.FIELD, product)
	}

	def hasSecurityType: Boolean = self.isSetField(SecurityType.FIELD)

	def securityType: String = self.getString(SecurityType.FIELD)

	def securityType_=(securityType: String) {
		self.setString(SecurityType.FIELD, securityType)
	}

	def instrument: Instrument = {
		Instrument(self)
	}

	def instrument_=(instrument: Instrument) {
		self.setFields(instrument.toFields)
	}

}
