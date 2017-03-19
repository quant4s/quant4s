package org.quant4s.data

import java.util.Date

import org.quant4s.data.market._

/**
  *
  */
class Slice(ptime: Date, pdata: Iterable[BaseData], ptradeBars: TradeBars, pticks: Ticks, hasData: Option[Boolean] = None) extends Iterable[(Symbol, BaseData)] {

  //  public Slice(DateTime time, IEnumerable<BaseData> data, TradeBars tradeBars, Ticks ticks, Splits splits, Dividends dividends, Delistings delistings, SymbolChangedEvents symbolChanges, bool? hasData = null)

  private val _bars = createCollection[TradeBars, TradeBar](ptradeBars)
  private val _ticks = _createTicksCollection(pticks)
  val time = ptime
  val data = pdata
  private lazy val _data: DataDictionary[SymbolData] = null
  private val _dataByType: Map[Object, Object] = null

  def bars = _bars

  private def createCollection[T <: DataDictionary[TItem], TItem <: BaseData](collection: T): T = {
    collection
  }

  private def _createTicksCollection(pticks: Ticks) = {
    if (pticks != null)
      pticks
    else {
      val ticks = new Ticks(time)
      for (d <- data)
        ticks.+(("", d.value)) // TODO:
      ticks
    }
  }

  override def iterator: Iterator[(Symbol, BaseData)] = {
    null
  }


  private object SubscriptionType extends Enumeration {
    type SubscriptionType = Value
    val TradeBar, Tick, Custom = Value
  }

  private class SymbolData(psymbol: Symbol) {
    val symbol = psymbol
    val ticks = List()
    val auxilliaryData = List[BaseData]()

    var custom: BaseData = null
    var tradeBar: TradeBar = null
    var subscriptionType: SubscriptionType.SubscriptionType = SubscriptionType.TradeBar

    def getData() = {
      subscriptionType match {
        case SubscriptionType.TradeBar => tradeBar
        case SubscriptionType.Tick => ticks
        case SubscriptionType.Custom => custom
      }
    }
  }

}


/*
* private T CreateCollection<T, TItem>(T collection)
            where T : DataDictionary<TItem>, new()
            where TItem : BaseData
        {
            if (collection != null) return collection;
            collection = new T();
#pragma warning disable 618 // This assignment is left here until the Time property is removed.
            collection.Time = Time;
#pragma warning restore 618
            foreach (var item in _data.Value.Values.Select(x => x.GetData()).OfType<TItem>())
            {
                collection[item.Symbol] = item;
            }
            return collection;
        }
        */