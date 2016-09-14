package com.bostontechnologies.quickfixs.fields

import quickfix.Group
import com.bostontechnologies.quickfixs.messages.RichFieldMap


trait RichGroup extends RichFieldMap[Group] {

  def toFields: Group
}