package com.bostontechnologies.quickfixs.fields

import com.bostontechnologies.quickfixs.components.Party
import quickfix.{Group, Message}
import quickfix.field.{PartyID, NoPartyIDs}
import scala.collection.JavaConversions._

trait RichParties {
  val self: Message

  def partyCount: Int = self.getInt(NoPartyIDs.FIELD)

  def parties: Seq[Party] = self.getGroups(NoPartyIDs.FIELD).map(Party(_))

  def +=(party: Party) {
    val group = new Group(NoPartyIDs.FIELD, PartyID.FIELD)
    group.setFields(party.toFields)
    self.addGroup(group)
  }

  def ++=(parties: Iterable[Party]) {
    parties.foreach(this += _)
  }
}