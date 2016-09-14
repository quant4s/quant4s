package com.msilb.scalanda.restapi.model

sealed trait WeeklyAlignment

object WeeklyAlignment {

  case object Monday extends WeeklyAlignment

  case object Tuesday extends WeeklyAlignment

  case object Wednesday extends WeeklyAlignment

  case object Thursday extends WeeklyAlignment

  case object Friday extends WeeklyAlignment

  case object Saturday extends WeeklyAlignment

  case object Sunday extends WeeklyAlignment

}
