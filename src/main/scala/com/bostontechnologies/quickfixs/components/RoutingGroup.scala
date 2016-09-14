package com.bostontechnologies.quickfixs.components

import com.bostontechnologies.quickfixs.fields.RichGroup
import quickfix.field.{NoRoutingIDs, RoutingID, RoutingType}
import quickfix.Group

class RoutingGroup(val self: Group) extends RichGroup {

  def groupId: String = self.getString(RoutingID.FIELD)

  def routingType = self.getInt(RoutingType.FIELD)

  def toFields = self
}

object RoutingGroup {

  val GroupFieldNumber = NoRoutingIDs.FIELD
  val GroupDelimiter = RoutingType.FIELD

  def apply(group: Group) = new RoutingGroup(group)

  def apply(groupId: String, routingType: Int = RoutingType.TARGET_LIST) = {
    val g = new Group(GroupFieldNumber, GroupDelimiter)
    g.setString(RoutingID.FIELD, groupId)
    g.setInt(RoutingType.FIELD, routingType)
    new RoutingGroup(g)
  }
}