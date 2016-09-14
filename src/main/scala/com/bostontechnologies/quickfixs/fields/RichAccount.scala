package com.bostontechnologies.quickfixs.fields

import quickfix.field.Account
import quickfix.Message

trait RichAccount {
  val self: Message

  def hasAccount: Boolean = self.isSetField(Account.FIELD)

  def account: String = self.getString(Account.FIELD)

  def account_=(value: String) {
    self.setString(Account.FIELD, value)
  }
}
