package quanter

import java.util.{Calendar, Date}

import org.apache.commons.math3.stat.correlation.{Covariance, PearsonsCorrelation}
import org.apache.commons.math3.stat.descriptive.moment.Variance
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

    def addTicks(x: Long) = {
      val cal = Calendar.getInstance()
      cal.setTime(time)
      cal.add(Calendar.MILLISECOND, x.toInt)
      val ret = cal.getTime()
      ret

    }

    def - (x:Date) : TimeSpan = {
      new TimeSpan(time.getTime() - x.getTime())
    }

    def >= (x: Date): Boolean = {
      true
    }
    def <= (x:Date): Boolean = {
      true
    }

    def roundDown(interval: TimeSpan): Date = {
      if (interval == TimeSpan.Zero) time
      else time.addTicks(-(time.getTime() % interval.milliSeconds));
    }
  }

  implicit class IterableExt(left: Iterable[Double]) {
    def variance(): Double = {
      val variance = new Variance()
      variance.evaluate(left.toArray)
    }

    def variance(period: Int): Double = {
      val variance = new Variance()
      variance.evaluate(left.toArray, period)
    }

    def covariance(yArray: Iterable[Double]): Double = {
      val covariance = new Covariance()
      covariance.covariance(left.toArray, yArray.toArray)
    }

    def pearsonsCorrelation(yArray: Iterable[Double]): Double = {
      val pc = new PearsonsCorrelation()
      pc.correlation(left.toArray, yArray.toArray)
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
