package jtrade.trader;

import java.util.List;

import jtrade.Symbol;

public interface Trader {

	/**
	 * Connect the trader to the marketplace and enable order executions.
	 */
	public void connect();

	/**
	 * Disconnect the trader from the marketplace.
	 */
	public void disconnect();

	/**
	 * Checks if the trader is connected.
	 * 
	 * @return true if connected else false
	 */
	public boolean isConnected();

	/**
	 * Set target position quantity using default ExecutionMethod
	 * 
	 * @param symbol
	 * @param quantity
	 * @return A Position pending processing
	 */
	public Position setPosition(Symbol symbol, int quantity);

	/**
	 * Set target position using default ExecutionMethod
	 * 
	 * @param symbol
	 * @param quantity
	 * @param executionMethod
	 * @return A Position pending processing
	 */
	public Position setPosition(Symbol symbol, int quantity, ExecutionMethod executionMethod);

	/**
	 * Cancel existing execution
	 */
	public void cancelExecution(Symbol symbol);

	/**
	 * Cancel all existing executions
	 */
	public void cancelExecutions();
	
	/**
	 * Get Portfolio.
	 *
	 * @return A Portfolio
	 */
	public Portfolio getPortfolio();

	/**
	 * Returns a snapshot of open orders which have not yet been filled.
	 * 
	 * @return A list of orders
	 */
	public List<OpenOrder> getOpenOrders();

	/**
	 * Returns a snapshot of open orders by specified symbol which have not yet
	 * been filled.
	 * 
	 * @param symbol
	 * @param orderType
	 * @return A list of orders
	 */
	public List<OpenOrder> getOpenOrders(Symbol symbol, OrderType orderType);

	/**
	 * Returns a matching open order if one exists.
	 * 
	 * @param symbol
	 * @param orderType
	 * @return An OpenOrder or null if no matching order exists
	 */
	public OpenOrder getOpenOrder(Symbol symbol, OrderType orderType);

	/**
	 * Sends an order to the market.
	 * 
	 * @param symbol
	 * @param action
	 * @param quantity
	 * @return An OpenOrder representing the state of the order
	 */
	public OpenOrder placeOrder(Symbol symbol, int quantity, String reference);

	/**
	 * Sends an order to the market.
	 * 
	 * @param symbol
	 * @param action
	 * @param quantity
	 * @return An OpenOrder representing the state of the order
	 */
	public OpenOrder placeOrder(Symbol symbol, int quantity, double price, String reference);

	/**
	 * Sends an order to the market, with support for stop orders.
	 * 
	 * @param symbol
	 * @param action
	 * @param type
	 * @param quantity
	 * @param price
	 * @param stopPercent
	 * @return
	 */
	public OpenOrder placeOrder(Symbol symbol, OrderType type, int quantity, double price, double stopPercent, String reference);

	/**
	 * Cancels the specified open order.
	 * 
	 * @param openOrder
	 */
	public void cancelOrder(OpenOrder openOrder);

	/**
	 * Cancels any matching open order.
	 * 
	 * @param symbol
	 * @param orderType
	 */
	public void cancelOrder(Symbol symbol, OrderType orderType);
	
	/**
	 * Cancels all open orders.
	 */
	public void cancelOrders();

	/**
	 * Adds an OrderListener to this trader.
	 * 
	 * @param listener
	 */
	public void addOrderListener(OrderListener listener);

	/**
	 * Removes an OrderListener to this trader.
	 * 
	 * @param listener
	 */
	public void removeOrderListener(OrderListener listener);

	
	/**
	 * Get commission used by this trader.
	 * 
	 * @return commission
	 */
	public Commission getCommission();
	
	/**
	 * Set commission used by this trader.
	 * 
	 * @param commission
	 */
	public void setCommission(Commission commission);
	
	/**
	 * Get the performance tracker associated with this trader.
	 * 
	 * @return performance tracker
	 */
	public PerformanceTracker getPerformanceTracker();
}
