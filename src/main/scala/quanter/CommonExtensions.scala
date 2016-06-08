package quanter

import java.util.{Calendar, Date}

import quanter.Resolution._

/**
  *
  */
object CommonExtensions {
  implicit class DateExt(time: Date) {
    def addDays(x: Int) =  {
      val cal = Calendar.getInstance()
      cal.setTime(time)
      cal.add(Calendar.DAY_OF_YEAR, x)
      val ret = cal.getTime()
      ret
    }

    def addSeconds(x: Int) = {
      val cal = Calendar.getInstance()
      cal.setTime(time)
      cal.add(Calendar.SECOND, x)
      val ret = cal.getTime()
      ret
    }

    def - (x:Date) : TimeSpan = {
      new TimeSpan(time.getTime() - x.getTime())
    }
  }

  implicit class ResolutionExt(resoltion: Resolution) {
    def toTimeSpan(): TimeSpan= {
      resoltion match {
        case Minute5 => TimeSpan.fromMinutes(5)
        case Minute30 => TimeSpan.fromMinutes(30)
        case Minute15 => TimeSpan.fromMinutes(15)
        case Hour => TimeSpan.fromHours(1)
        case Daily => TimeSpan.fromDays(1)
        case Tick => TimeSpan.fromTicks(0)
      }
    }

  }
}
