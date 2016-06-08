package quanter.domain

import java.util.Date

import scala.collection.mutable.ArrayBuffer

/**
  *
  */
class Portfolio {
  var cash = 1000.0
  var holdings = new ArrayBuffer[SecurityHolding]()

  var date: Date = new Date()
}
