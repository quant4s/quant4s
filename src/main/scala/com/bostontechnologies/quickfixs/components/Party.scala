package com.bostontechnologies.quickfixs.components

import quickfix.{Message, FieldMap}
import quickfix.field.{PartyRole, PartyIDSource, PartyID}

class Party(self: FieldMap) {

  def id: String = self.getString(PartyID.FIELD)

  def id_=(value: String) {
    self.setString(PartyID.FIELD, value)
  }

  def source: Char = self.getChar(PartyIDSource.FIELD)

  def source_=(value: Char) {
    self.setChar(PartyIDSource.FIELD, value)
  }

  def role: Int = self.getInt(PartyRole.FIELD)

  def role_=(value: Int) {
    self.setInt(PartyRole.FIELD, value)
  }

  def toFields: FieldMap = self
}

object Party {

  def apply(fieldMap: FieldMap): Party =
    Party(
      fieldMap.getString(PartyID.FIELD),
      fieldMap.getChar(PartyIDSource.FIELD),
      fieldMap.getInt(PartyRole.FIELD)
    )

  def apply(id: String, source: Char, role: Int): Party = {
    val party = new Party(new Message)

    party.id = id
    party.source = source
    party.role = role

    party
  }
}