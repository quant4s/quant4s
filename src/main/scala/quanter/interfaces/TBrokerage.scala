package quanter.interfaces

/**
  *
  */
trait TBrokerage {
  /// <summary>
  /// Event that fires each time an order is filled
  /// </summary>
//  event EventHandler<OrderEvent> OrderStatusChanged;

  /// <summary>
  /// Event that fires each time a user's brokerage account is changed
  /// </summary>
//  event EventHandler<AccountEvent> AccountChanged;

  /// <summary>
  /// Event that fires when a message is received from the brokerage
  /// </summary>
//  event EventHandler<BrokerageMessageEvent> Message;

  def name: String
  def isConnected: Boolean

  def connect
  def disconnect
  def keep
  /// <summary>
  /// Gets all open orders on the account
  /// </summary>
  /// <returns>The open orders returned from IB</returns>
//  List<Order> GetOpenOrders();

  /// <summary>
  /// Gets all holdings for the account
  /// </summary>
  /// <returns>The current holdings from the account</returns>
//  List<Holding> GetAccountHoldings();

  /// <summary>
  /// Gets the current cash balance for each currency held in the brokerage account
  /// </summary>
  /// <returns>The current cash balance for each currency available for trading</returns>
//  List<Cash> GetCashBalance();

  /// <summary>
  /// Places a new order and assigns a new broker ID to the order
  /// </summary>
  /// <param name="order">The order to be placed</param>
  /// <returns>True if the request for a new order has been placed, false otherwise</returns>
//  bool PlaceOrder(Order order);

  /// <summary>
  /// Updates the order with the same id
  /// </summary>
  /// <param name="order">The new order information</param>
  /// <returns>True if the request was made for the order to be updated, false otherwise</returns>
//  bool UpdateOrder(Order order);

  /// <summary>
  /// Cancels the order with the specified ID
  /// </summary>
  /// <param name="order">The order to cancel</param>
  /// <returns>True if the request was made for the order to be canceled, false otherwise</returns>
//  bool CancelOrder(Order order);


}

