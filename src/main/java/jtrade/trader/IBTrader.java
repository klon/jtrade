package jtrade.trader;

import java.io.File;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jtrade.JTradeException;
import jtrade.Symbol;
import jtrade.SymbolFactory;
import jtrade.marketfeed.IBMarketFeed;
import jtrade.marketfeed.Tick;
import jtrade.util.Configurable;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import com.ib.client.Contract;
import com.ib.client.Execution;
import com.ib.client.Order;
import com.ib.client.OrderState;

public class IBTrader extends IBMarketFeed implements Trader {
	private static final Logger logger = LoggerFactory.getLogger(IBTrader.class);
	private static final Logger blotter = LoggerFactory.getLogger("blotter");

	public final Configurable<String> ACCOUNT_CODE = new Configurable<String>("ACCOUNT_CODE", "");
	public final Configurable<Boolean> REAL_MONEY_ACCOUNT = new Configurable<Boolean>("REAL_MONEY_ACCOUNT", false);

	protected AtomicInteger nextValidOrderId;
	protected String accountCode;
	protected Map<Integer, OpenOrder> openOrdersById;
	protected List<OrderListener> orderListeners;
	protected Portfolio portfolio;
	protected Commission commission;

	public IBTrader() {
		this((String) null, -1, (File) null);
	}

	public IBTrader(int clientId) {
		this((String) null, clientId, (File) null);
	}

	public IBTrader(String host, int clientId) {
		this(host, clientId, (File) null);
	}

	public IBTrader(String host, int clientId, String dataDir) {
		this(host, clientId, new File(dataDir));
	}

	public IBTrader(String host, int clientId, File dataDir) {
		super(host, clientId);
		openOrdersById = new HashMap<Integer, OpenOrder>();
		orderListeners = new ArrayList<OrderListener>();
		portfolio = new Portfolio(this);
	}

	@Override
	protected synchronized void doConnect() {
		super.doConnect();
		if (isConnected()) {
			logger.info("Requesting account updates for {}", ACCOUNT_CODE.get());
			socket.reqAccountUpdates(true, ACCOUNT_CODE.get());
			socket.reqOpenOrders();
			try {
				while (accountCode == null) {
					wait();
				}
			} catch (InterruptedException e) {
				throw new JTradeException(e);
			}
			commission = new Commission(0.0, 0.0, 0.0, 0.0, 0.0);
			
			if (!ACCOUNT_CODE.get().equals(accountCode)) {
				logger.info("Account code does not match specified account {} <> {}, exiting.", accountCode, ACCOUNT_CODE.get());
				disconnect();
			} else if (!accountCode.startsWith("D")) {
				if (!REAL_MONEY_ACCOUNT.get()) {
					logger.info("Connected to real money account {} without #REAL_MONEY_ACCOUNT set to true, exiting.", accountCode);
					disconnect();
				}
				logger.info("Connected to real money account {} (Manage risk accordingly!)", accountCode);
			} else {
				logger.info("Connected to paper money account {}", accountCode);
			}
		}
	}

	@Override
	public synchronized void addOrderListener(OrderListener listener) {
		logger.info("Adding OrderListener {}", listener.getClass().getSimpleName());
		orderListeners.add(listener);
	}

	@Override
	public synchronized void removeOrderListener(OrderListener listener) {
		boolean removed = orderListeners.remove(listener);
		if (removed) {
			logger.info("Removed OrderListener {}", listener.getClass().getSimpleName());
		}
	}

	@Override
	public synchronized void removeAllListeners() {
		super.removeAllListeners();
		orderListeners.clear();
	}

	protected Order makeOrder(OpenOrder openOrder) {
		Order order = new Order();
		order.m_orderRef = openOrder.getReference() != null ? openOrder.getReference() : "";
		order.m_overridePercentageConstraints = true;
		order.m_totalQuantity = Math.abs(openOrder.getQuantity());
		if (openOrder.isBuy()) {
			order.m_action = "BUY";
		} else if (openOrder.isSell()) {
			order.m_action = "SELL";
		}
		if (openOrder.isMarket()) {
			order.m_orderType = "MKT";
		} else if (openOrder.isLimit()) {
			order.m_orderType = "LMT";
			order.m_lmtPrice = openOrder.getPrice();
		} else if (openOrder.isStopMarket()) {
			order.m_orderType = "STP";
			order.m_triggerMethod = 8; // midpoint
			order.m_auxPrice = openOrder.getStopPrice();
		} else if (openOrder.isStopLimit()) {
			order.m_orderType = "STPLMT";
			order.m_triggerMethod = 8; // midpoint
			order.m_lmtPrice = openOrder.getPrice();
			order.m_auxPrice = openOrder.getStopPrice();
		} else if (openOrder.isTrailMarket()) {
			order.m_orderType = "TRAIL";
			order.m_triggerMethod = 8; // midpoint
			order.m_auxPrice = openOrder.getTrailStopOffset();
		} else if (openOrder.isTrailLimit()) {
			order.m_orderType = "TRAILLMT";
			order.m_triggerMethod = 8; // midpoint
			order.m_lmtPrice = openOrder.getPrice();
			order.m_trailStopPrice = openOrder.getStopPrice();
			order.m_auxPrice = openOrder.getTrailStopOffset();
		}
		return order;
	}

	protected OpenOrder toOpenOrder(Contract contract, Order order) {
		int orderId = order.m_orderId;
		int quantity = order.m_totalQuantity;
		if ("SELL".equals(order.m_action)) {
			quantity = -quantity;
		}
		OrderType type = null;
		double price = -1;
		double stopPrice = -1;
		double trailStopOffset = -1;
		if ("MKT".equals(order.m_orderType)) {
			type = OrderType.MARKET;
		} else if ("LMT".equals(order.m_orderType)) {
			type = OrderType.LIMIT;
			price = order.m_lmtPrice;
		} else if ("STP".equals(order.m_orderType)) {
			type = OrderType.STOP_MARKET;
			stopPrice = order.m_auxPrice;
		} else if ("STPLMT".equals(order.m_orderType)) {
			type = OrderType.STOP_LIMIT;
			price = order.m_lmtPrice;
			stopPrice = order.m_auxPrice;
		} else if ("TRAIL".equals(order.m_orderType)) {
			type = OrderType.TRAIL_MARKET;
			order.m_triggerMethod = 8; // midpoint
			stopPrice = order.m_trailStopPrice;
			trailStopOffset = order.m_auxPrice;
		} else if ("TRAILLMT".equals(order.m_orderType)) {
			type = OrderType.TRAIL_LIMIT;
			price = order.m_lmtPrice;
			stopPrice = order.m_trailStopPrice;
			trailStopOffset = order.m_auxPrice;
		}
		OpenOrder openOrder = new OpenOrder(orderId, toSymbol(contract), type, quantity, price, stopPrice, trailStopOffset, new DateTime(), order.m_orderRef);
		return openOrder;
	}

	@Override
	public List<OpenOrder> getOpenOrders() {
		return new ArrayList<OpenOrder>(openOrdersById.values());
	}

	@Override
	public List<OpenOrder> getOpenOrders(Symbol symbol, OrderType orderType) {
		List<OpenOrder> openOrders = new ArrayList<OpenOrder>(openOrdersById.size());
		for (OpenOrder o : openOrdersById.values()) {
			if ((symbol == null || symbol.equals(o.getSymbol())) && (orderType == null || orderType.equals(o.getType()))) {
				openOrders.add(o);
			}
		}
		return openOrders;
	}

	@Override
	public OpenOrder getOpenOrder(Symbol symbol, OrderType orderType) {
		if (symbol == null) {
			throw new IllegalArgumentException("Symbol cannot be null");
		}
		if (orderType == null) {
			throw new IllegalArgumentException("OrderType cannot be null");
		}
		for (OpenOrder o : openOrdersById.values()) {
			if (symbol.equals(o.getSymbol()) && orderType.equals(o.getType())) {
				return o;
			}
		}
		return null;
	}

	@Override
	public synchronized OpenOrder placeOrder(Symbol symbol, int quantity, String reference) {
		return placeOrder(symbol, OrderType.MARKET, quantity, -1, -1, reference);
	}

	@Override
	public synchronized OpenOrder placeOrder(Symbol symbol, int quantity, double price, String reference) {
		return placeOrder(symbol, OrderType.LIMIT, quantity, price, -1, reference);
	}

	@Override
	public synchronized OpenOrder placeOrder(Symbol symbol, OrderType type, int quantity, double price, double stopPercent, String reference) {
		if (quantity == 0) {
			throw new IllegalArgumentException(String.format("Invalid quantity %s", quantity));
		}
		if ((OrderType.LIMIT.equals(type) || OrderType.STOP_LIMIT.equals(type) || OrderType.TRAIL_LIMIT.equals(type)) && price <= 0) {
			throw new IllegalArgumentException(String.format("Invalid limit order price %s", price));
		}
		if ((OrderType.STOP_MARKET.equals(type) || OrderType.STOP_LIMIT.equals(type) || OrderType.TRAIL_MARKET.equals(type) || OrderType.TRAIL_LIMIT.equals(type))
				&& stopPercent <= 0) {
			throw new IllegalArgumentException(String.format("Invalid stop order with stop percent %s", stopPercent));
		}
		checkConnected();

		OpenOrder openOrder = getOpenOrder(symbol, type);
		if (openOrder != null && !openOrder.isFilled() && !openOrder.isCancelled()) {
			throw new IllegalStateException("OpenOrder for same strategy, symbol and type already exists: " + openOrder);
		}

		DateTime now = new DateTime();

		double stopPrice = -1;
		double trailingStopOffset = -1;
		if (OrderType.STOP_MARKET.equals(type) || OrderType.STOP_LIMIT.equals(type) || OrderType.TRAIL_MARKET.equals(type) || OrderType.TRAIL_LIMIT.equals(type)) {
			Tick t = getLastTick(symbol);
			if (t == null) {
				t = getLastTick(symbol, now);
				if (t == null) {
					throw new IllegalStateException(String.format("Cannot set stop without tick data for symbol %s", symbol));
				}
			}
			trailingStopOffset = Util.round(t.getMidPrice() * stopPercent, 10.0);
			if (quantity < 0) {
				stopPrice = t.getMidPrice() - trailingStopOffset;
			} else {
				stopPrice = t.getMidPrice() + trailingStopOffset;
			}
			stopPrice = Util.round(stopPrice, 2.0);
		}
		int orderId = nextValidOrderId.getAndIncrement();
		openOrder = new OpenOrder(orderId, symbol, type, quantity, price, stopPrice, trailingStopOffset, new DateTime(), reference);
		openOrdersById.put(orderId, openOrder);

		Contract contract = makeContract(symbol);
		Order order = makeOrder(openOrder);

		logger.info("Placing order {}", openOrder);

		socket.placeOrder(orderId, contract, order);
		for (OrderListener listener : orderListeners) {
			try {
				listener.onOrderPlaced(openOrder);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
		}
		return openOrder;
	}

	@Override
	public synchronized void cancelOrder(Symbol symbol, OrderType orderType) {
		List<OpenOrder> openOrders = getOpenOrders(symbol, orderType);
		if (openOrders.isEmpty()) {
			logger.info("Cannot cancel order, no open order found for {} {}", new Object[] { symbol, orderType });
			return;
		}
		for (OpenOrder o : openOrders) {
			cancelOrder(o);
		}
	}

	@Override
	public synchronized void cancelOrder(OpenOrder openOrder) {
		checkConnected();
		socket.cancelOrder(openOrder.getOrderId());
	}
	
	@Override
	public void cancelOrders() {
		for (OpenOrder o : openOrdersById.values()) {
			cancelOrder(o);
		}
	}

	protected void logExecution(OpenOrder openOrder, int quantity) {
		Object[] params = new Object[] { openOrder.getFillDate(), "EXEC", openOrder.getAction(), openOrder.getType(), quantity, openOrder.getSymbol(),
				openOrder.getSymbol().getCurrency(), openOrder.getLastFillPrice(), "", "", "", "", "",
				openOrder.getReference() != null ? openOrder.getReference() : "", accountCode };
		blotter.info(MarkerFactory.getMarker("EXECUTION"), "{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}", params);
	}

	protected void logTrade(OpenOrder openOrder, int position, double costBasis, double realized, double unrealized) {
		Object[] params = new Object[] { openOrder.getFillDate(), "TRADE", openOrder.getAction(), openOrder.getType(), openOrder.getQuantityFilled(),
				openOrder.getSymbol(), openOrder.getSymbol().getCurrency(), openOrder.getAvgFillPrice(), position, Util.round(costBasis, 4),
				Util.round(realized, 4), Util.round(unrealized, 4), Util.round(openOrder.getCommission(), 4),
				openOrder.getReference() != null ? openOrder.getReference() : "", accountCode };
		blotter.info(MarkerFactory.getMarker("TRADE"), "{},{},{},{},{},{},{},{},{},{},{},{},{},{},{}", params);
	}

	private void closeOpenOrder(OpenOrder openOrder, double commissionAmount) {
		openOrdersById.remove(openOrder.getOrderId());
		if (Double.isNaN(commissionAmount) || commissionAmount <= 0.0) {
			commissionAmount = commission.calculate(openOrder.getQuantityFilled(), openOrder.getAvgFillPrice());
		}
		openOrder.setCommission(commissionAmount);
		logger.info("{} {}", openOrder.isFilled() ? "Filled" : "Partially filled", openOrder);

		Position position = portfolio.getPosition(openOrder.getSymbol());
		double[] values = position.update(openOrder.getFillDate(), openOrder.getQuantityFilled(), openOrder.getAvgFillPrice(), openOrder.getCommission());

		logTrade(openOrder, position.getQuantity(), values[0], values[1], values[2]);

		for (OrderListener listener : orderListeners) {
			try {
				listener.onOrderFilled(openOrder, position);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
		}
	}

	@Override
	public void nextValidId(int nextValidOrderId) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("nextValidId: {}", nextValidOrderId);

			this.nextValidOrderId = new AtomicInteger(nextValidOrderId);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void execDetails(int reqId, Contract contract, Execution execution) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("execDetails: {} {} {}", new Object[] { reqId, Util.toString(contract), Util.toString(execution) });

			OpenOrder openOrder = openOrdersById.get(execution.m_orderId);
			DateTime dt = DateTimeFormat.forPattern("yyyyMMdd  HH:mm:ss").parseDateTime(execution.m_time);
			if (openOrder != null) {
				int quantityChange = openOrder.isBuy() ? execution.m_shares : -execution.m_shares;
				openOrder.update(quantityChange, execution.m_price, dt);
				logExecution(openOrder, quantityChange);
			} else {
				logger.info("Execution does not match any open order {} {}", Util.toString(contract), Util.toString(execution));
			}
		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void execDetailsEnd(int reqId) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("execDetailsEnd: {}", reqId);

		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice,
			int clientId, String whyHeld) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("orderStatus: {} {} {} {} {} {} {} {} {} {}", new Object[] { orderId, status, filled, remaining, avgFillPrice, permId, parentId,
						lastFillPrice, clientId, whyHeld });

			if ("Cancelled".equals(status)) {
				OpenOrder openOrder = openOrdersById.remove(orderId);
				if (openOrder != null) {
					openOrder.setCancelled();
					logger.info("Cancelled {}", openOrder);
					for (OrderListener listener : orderListeners) {
						try {
							listener.onOrderCancelled(openOrder);
						} catch (Throwable t) {
							logger.error(t.getMessage(), t);
						}
					}
					if ((openOrder.isFilled() || (!openOrder.isOpen() && openOrder.getQuantityFilled() > 0))) {
						closeOpenOrder(openOrder, 0);
					}
				} else {
					logger.warn("Cancelled order {}, no matching order found!", orderId);
				}
			}

		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("openOrder: {} {} {} {}",
						new Object[] { orderId, Util.toString(contract), Util.toString(order), Util.toString(orderState) });

			OpenOrder openOrder = openOrdersById.get(orderId);
			if (openOrder != null) {
				if ((openOrder.isFilled() || (!openOrder.isOpen() && openOrder.getQuantityFilled() > 0)) && orderState.m_commission != Double.MAX_VALUE) {
					closeOpenOrder(openOrder, orderState.m_commission);
				}
			} else {
				if ("PendingSubmit".equals(orderState.m_status) || "PreSubmitted".equals(orderState.m_status) || "Submitted".equals(orderState.m_status)) {
					openOrder = toOpenOrder(contract, order);
					openOrdersById.put(orderId, openOrder);
					logger.info("Added {}", openOrder);
				}
			}
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void openOrderEnd() {
		try {
			if (logger.isDebugEnabled())
				logger.debug("openOrderEnd");

		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void updateAccountValue(String key, String value, String currency, String accountName) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("updateAccountValue: {} {} {} {}", new Object[] { key, value, currency, accountName });

			if ("AccountCode".equals(key)) {
				synchronized (this) {
					this.accountCode = value;
					notifyAll();
				}
			} else if ("AvailableFunds".equalsIgnoreCase(key) && isCurrencyCode(currency) && Util.isDouble(value)) {
				portfolio.setCash(currency, Double.parseDouble(value));
			} else if ("BuyingPower".equalsIgnoreCase(key) && isCurrencyCode(currency) && Util.isDouble(value)) {
				portfolio.setBaseCurrency(currency);
			} else if ("ExchangeRate".equalsIgnoreCase(key) && isCurrencyCode(currency) && Util.isDouble(value)) {
				portfolio.setExchangeRate(currency, Double.parseDouble(value));
			}

		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void updatePortfolio(Contract contract, int qty, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL,
			String accountName) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("updatePortfolio: {} {} {} {} {} {} {} {}", new Object[] { Util.toString(contract), qty, marketPrice, marketValue, averageCost,
						unrealizedPNL, realizedPNL, accountName });
			if (qty != 0) {
				Symbol symbol = toSymbol(contract);
				double costBasis = averageCost / (contract.m_multiplier != null ? Integer.parseInt(contract.m_multiplier) : 1);
				Position position = new Position(symbol, qty, costBasis, 0.0);
				portfolio.setPosition(symbol, position);
				logger.info("Updated {}, last: {}, unrealized pnl: {}", new Object[] { position, marketPrice, position.getProfitLoss(marketPrice) });
			}
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void accountDownloadEnd(String accountName) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("accountDownloadEnd: {}", accountName);
			logger.info("Updated {}", portfolio);
			socket.reqAccountUpdates(false, ACCOUNT_CODE.get());
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void updateAccountTime(String timeStamp) {
		try {
			if (logger.isDebugEnabled())
				logger.debug("updateAccountTime: {}", timeStamp);

		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	@Override
	public void error(int reqId, int errorCode, String errorMsg) {
		try {
			Object req = openOrdersById.get(reqId);
			if (req == null) {
				super.error(reqId, errorCode, errorMsg);
				return;
			}
			OpenOrder openOrder = (OpenOrder) req;

			String message = errorCode + ": " + errorMsg;
			lastMessage = message;

			switch (errorCode) {
			case 161:
				// 161: Cancel attempted when order is not in a cancellable
				// state
				logger.warn("Received error for {}: {}", openOrder, message);
				break;
			case 202:
				// 202: Order Cancelled
				break;
			case 110:
				// 110: price does not conform to the minimum price variation
				// for this
				// contract
			default:
				openOrder.setFailed();
				openOrdersById.remove(reqId);
				logger.warn("Received error for {}: {}", openOrder, message);
			}
		} catch (Throwable t) {
			// Do not allow exceptions come back to the socket -- it will cause
			// disconnects
			logger.error(t.getMessage(), t);
		}
	}

	private static boolean isCurrencyCode(String str) {
		try {
			return (Currency.getInstance(str.toUpperCase()) != null);
		} catch (IllegalArgumentException e) {
		}
		return false;
	}

	@Override
	public Position setPosition(Symbol symbol, int quantity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Position setPosition(Symbol symbol, int quantity, ExecutionMethod executionMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelExecution(Symbol symbol) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelExecutions() {
		// TODO Auto-generated method stub

	}

	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	@Override
	public Commission getCommission() {
		return commission;
	}
	
	@Override
	public void setCommission(Commission commission) {
		this.commission = commission;
	}
	
	public static void main(String[] args) {
		Trader trader = new IBTrader("localhost:4000", 22);

		Configurable.configure("jtrade.trader.IBTrader#ACCOUNT_CODE", "DU66791");

		try {
			// Symbol s = SymbolFactory.getESFutureSymbol(new DateTime());
			Symbol s = SymbolFactory.getSymbol("AAPL-SMART-USD-STOCK");

			trader.connect();
			Thread.sleep(2000);

			try {
				trader.placeOrder(s, 1, "testref1");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread.sleep(2000);

			try {
				trader.placeOrder(s, -1, "testref2");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Thread.sleep(2000);

			trader.cancelOrder(s, null);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			trader.disconnect();
		}
	}

	@Override
	public PerformanceTracker getPerformanceTracker() {
		return null;
	}
}
