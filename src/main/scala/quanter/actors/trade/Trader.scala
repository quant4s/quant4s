package quanter.actors.trade

import quanter.rest.{CancelOrder, Order}

/**
  *
  */
trait Trader {
  def queryCapital()

  def queryPosition(symbol: String)

  def queryOrders()

  def queryUnfinishOrders()

  def cancel(order: CancelOrder)

  def order(order: Order)


//
//  public abstract void enquiry(Order paramOrder, OrderListener paramOrderListener);
//
//  public abstract void answer(Order paramOrder, OrderListener paramOrderListener);
//
//  public abstract void addEnquiryListener(String paramString, EnquiryListener paramEnquiryListener);
//
//  public abstract void removeEnquiryListener(String paramString, EnquiryListener paramEnquiryListener);
//
//  public abstract void order(Order paramOrder, OrderListener paramOrderListener);
//
//  public abstract void trigger(Order paramOrder, OrderListener paramOrderListener);
//
//  public abstract void cancel(String paramString);
//
//  public abstract void cancelAll(String paramString1, String paramString2);
//
//  public abstract void removeOrderListener(String paramString);
//
//  public abstract void release();
//
//  public abstract List<OrderRelevant> queryOrders(String paramString);
//
//  public abstract List<OrderRelevant> queryUnfinishOrders(String paramString);
//
//  public abstract List<OrderDealResult> queryOrderDeals(String paramString);
//
//  public abstract void cancel(String paramString1, String paramString2, String paramString3);
//
//  public abstract void queryEac(String paramString1, String paramString2, EacListener paramEacListener);
//
//  public abstract Set<FuturesOptions> queryOptions(String paramString);
//
//  public abstract Set<Stock> queryStocks(String paramString);
//
//  public abstract Set<StockOptions> queryStockOptions(String paramString);
//
//  public abstract Set<Contract> queryEsunny(String paramString);
//
//  @Deprecated
//  public abstract HandChargeMargin queryContractMessage(String paramString1, String paramString2);
//
//  public abstract HandCharge queryContractHandCharge(String paramString1, String paramString2);
//
//  public abstract Margin queryContractMargin(String paramString1, String paramString2);
//
//  public abstract void bind(String paramString, int paramInt);
//
//  public abstract void start();
//
//  public abstract void stop();
//
//  public abstract void release(String paramString);
//
//  public abstract void send(String paramString);
//
//  public abstract void setName(String paramString);
//
//  public abstract String getName();
//
//  public abstract void assertVersionCompatible()
//  throws Exception;
//
//  public abstract int getConnectNum();
//
//  public abstract String getPlatformCode();
//
//  public abstract String getEnvironmentType();
//
//  public abstract void setEnvironmentType(String paramString);
//
//  public abstract void cleanUpDaily();
}
