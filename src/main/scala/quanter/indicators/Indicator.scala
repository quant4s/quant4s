package quanter.indicators

abstract class Indicator(name: String) extends IndicatorBase[IndicatorDataPoint](name){
  def toJson: String = ""
}
