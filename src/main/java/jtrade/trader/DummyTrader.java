package jtrade.trader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jtrade.Symbol;
import jtrade.marketfeed.Bar;
import jtrade.marketfeed.BarListener;
import jtrade.marketfeed.IBMarketFeed;
import jtrade.marketfeed.MarketFeed;
import jtrade.marketfeed.MarketListener;
import jtrade.marketfeed.Tick;
import jtrade.marketfeed.TickListener;
import jtrade.util.Configurable;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

public class DummyTrader implements Trader, MarketListener, BarListener, TickListener {
	private static final Logger logger = LoggerFactory.getLogger(DummyTrader.class);
	private static final Logger blotter = LoggerFactory.getLogger("blotter");

	public final Configurable<Integer> EXECUTION_DELAY_MILLIS = new Configurable<Integer>("EXECUTION_DELAY_MILLIS", 500);
	public final Configurable<String> BASE_CURRENCY = new Configurable<String>("BASE_CURRENCY", "EUR");
	public final Configurable<Double> INITIAL_CAPITAL = new Configurable<Double>("INITIAL_CAPITAL", 10000.0);
	public final Configurable<Double> EXCHANGE_RATE_USD = new Configurable<Double>("EXCHANGE_RATE_USD", 1.0);
	public final Configurable<Double> EXCHANGE_RATE_SEK = new Configurable<Double>("EXCHANGE_RATE_SEK", 1.0);
	public final Configurable<Double> EXCHANGE_RATE_EUR = new Configurable<Double>("EXCHANGE_RATE_EUR", 1.0);
	public final Configurable<Double> EXCHANGE_RATE_GBP = new Configurable<Double>("EXCHANGE_RATE_GBP", 1.0);
	public final Configurable<Double> COMMISSION_MIN = new Configurable<Double>("COMMISSION_MIN", 0.0);
	public final Configurable<Double> COMMISSION_MAX = new Configurable<Double>("COMMISSION_MIN", 0.0);
	public final Configurable<Double> COMMISSION_PER_SHARE = new Configurable<Double>("COMMISSION_MIN", 0.0);
	public final Configurable<Double> COMMISSION_RATE = new Configurable<Double>("COMMISSION_MIN", 0.0);
	public final Configurable<Boolean> VERBOSE = new Configurable<Boolean>("VERBOSE", false);
	
	protected MarketFeed marketFeed;
	protected List<OrderListener> orderListeners;
	protected boolean connected;
	protected int nextValidOrderId;
	protected Map<Integer, OpenOrder> openOrdersById;
	protected DateTime lastProcessOrder;
	protected Portfolio portfolio;
	protected PerformanceTracker performanceTracker;
	protected Commission commission;

	public DummyTrader(MarketFeed marketFeed) {
		this.marketFeed = marketFeed;
		orderListeners = new ArrayList<OrderListener>();
		openOrdersById = new ConcurrentHashMap<Integer, OpenOrder>();
		portfolio = new Portfolio(marketFeed);
		portfolio.setBaseCurrency(BASE_CURRENCY.get());
		portfolio.setCash(BASE_CURRENCY.get(), INITIAL_CAPITAL.get());
		portfolio.setExchangeRate("USD", EXCHANGE_RATE_USD.get());
		portfolio.setExchangeRate("SEK", EXCHANGE_RATE_SEK.get());
		portfolio.setExchangeRate("EUR", EXCHANGE_RATE_EUR.get());
		portfolio.setExchangeRate("GBP", EXCHANGE_RATE_GBP.get());
		performanceTracker = new PerformanceTracker(marketFeed);
		commission = new Commission(0.0, 0.0, 0.0, 0.0, 0.0);
		if (marketFeed instanceof IBMarketFeed) {
			Configurable.configure(VERBOSE, true);
		}
	}
	
	@Override
	public Portfolio getPortfolio() {
		return portfolio;
	}
	
	@Override
	public PerformanceTracker getPerformanceTracker() {
		return performanceTracker;
	}

	@Override
	public void connect() {
		if (VERBOSE.get()) {
			logger.info("Connected");
		}
		connected = true;
		marketFeed.addMarketListener(this);
		try {
			marketFeed.addBarListener(this);
		} catch (UnsupportedOperationException e) {
			marketFeed.addTickListener(this);
		}
	}

	@Override
	public void disconnect() {
		if (VERBOSE.get()) {
			logger.info("Disconnected");
		}
		connected = false;
		try {
			marketFeed.removeBarListener(this);
		} catch (UnsupportedOperationException e) {
			marketFeed.removeTickListener(this);
		}

	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	public void reset() {
		logger.info("Reset");
		orderListeners.clear();
		openOrdersById.clear();
		nextValidOrderId = 0;
	}

	@Override
	public void addOrderListener(OrderListener listener) {
		orderListeners.add(listener);
	}

	@Override
	public void removeOrderListener(OrderListener listener) {
		orderListeners.remove(listener);
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
	public void cancelOrder(Symbol symbol, OrderType orderType) {
		List<OpenOrder> openOrders = getOpenOrders(symbol, orderType);
		if (openOrders.isEmpty()) {
			if (VERBOSE.get()) {
				logger.info("Cannot cancel order, no open order found for {} {}", new Object[] { symbol, orderType });
			}
			return;
		}
		for (OpenOrder o : openOrders) {
			cancelOrder(o);
		}
	}

	@Override
	public void cancelOrder(OpenOrder openOrder) {
		openOrder.setCancelled();
		if (VERBOSE.get()) {
			logger.info("Cancelled order {}", openOrder);
		}
		for (OrderListener listener : orderListeners) {
			try {
				listener.onOrderCancelled(openOrder);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
		}
	}
	
	@Override
	public void cancelOrders() {
		for (OpenOrder o : openOrdersById.values()) {
			cancelOrder(o);
		}
	}

	@Override
	public OpenOrder placeOrder(Symbol symbol, int quantity, String reference) {
		return placeOrder(symbol, OrderType.MARKET, quantity, -1, -1, reference);
	}

	@Override
	public OpenOrder placeOrder(Symbol symbol, int quantity, double price, String reference) {
		return placeOrder(symbol, OrderType.LIMIT, quantity, price, -1, reference);
	}

	@Override
	public OpenOrder placeOrder(Symbol symbol, OrderType type, int quantity, double price, double stopPercent, String reference) {
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
		Tick lastTick = marketFeed.getLastTick(symbol);
		if (lastTick == null) {
			throw new IllegalStateException(String.format("Could not find price tick for symbol %s while placing order.", symbol));
		}

		OpenOrder openOrder = getOpenOrder(symbol, type);
		if (openOrder != null && !openOrder.isFilled() && !openOrder.isCancelled()) {
			return openOrder;//throw new IllegalStateException("OpenOrder for same symbol and type already exists: " + openOrder);
		}

		double stopPrice = -1;
		double trailingStopOffset = -1;
		if (OrderType.STOP_MARKET.equals(type) || OrderType.STOP_LIMIT.equals(type) || OrderType.TRAIL_MARKET.equals(type) || OrderType.TRAIL_LIMIT.equals(type)) {
			trailingStopOffset = Util.round(lastTick.getMidPrice() * stopPercent, 10.0);
			if (quantity < 0) {
				stopPrice = lastTick.getMidPrice() - trailingStopOffset;
			} else {
				stopPrice = lastTick.getMidPrice() + trailingStopOffset;
			}
			stopPrice = Util.round(stopPrice, 2.0);
		}

		int orderId = ++nextValidOrderId;
		openOrder = new OpenOrder(orderId, symbol, type, quantity, price, stopPrice, trailingStopOffset, lastTick.getDateTime(), reference);
		openOrdersById.put(orderId, openOrder);

		if (VERBOSE.get()) {
			logger.info("Placed {}", openOrder);
		}

		for (OrderListener listener : orderListeners) {
			try {
				listener.onOrderPlaced(openOrder);
			} catch (Throwable t) {
				logger.warn(t.getMessage(), t);
			}
		}
		return openOrder;
	}

	protected void processOpenOrders(DateTime dt) {
		if (lastProcessOrder != null && !dt.isAfter(lastProcessOrder)) {
			return;
		}
		if (openOrdersById.isEmpty()) {
			return;
		}

		for (Iterator<OpenOrder> openOrders = openOrdersById.values().iterator(); openOrders.hasNext();) {
			OpenOrder openOrder = openOrders.next();
			if (!openOrder.isOpen()) {
				openOrders.remove();
				continue;
			}
			if (dt.getMillis() - openOrder.getOrderDate().getMillis() < EXECUTION_DELAY_MILLIS.get()) {
				continue;
			}
			Symbol symbol = openOrder.getSymbol();
			Tick lastTick = marketFeed.getLastTick(symbol);
			if (lastTick == null) {
				logger.debug("Cannot process order {}, no tick for symbol at %s", openOrder, dt);
				continue;
			}
			if (dt.getMillisOfDay() != 0 && (dt.getMillis() - lastTick.getDateTime().getMillis()) > 5 * 60 * 1000) {
				logger.debug("Cannot process order {}, {}, stale market data", openOrder, lastTick);
				continue;
			}
			if (lastTick.getAsk() <= 0 || lastTick.getBid() <= 0) {
				logger.debug("Cannot process order {}, {} is not valid", openOrder, lastTick);
				continue;
			}



			boolean fill = false;
			switch (openOrder.getType()) {
			case MARKET:
				fill = true;
				break;
			case LIMIT:
				fill = ((openOrder.isBuy() && lastTick.getAsk() <= openOrder.getPrice()) || (openOrder.isSell() && lastTick.getBid() >= openOrder.getPrice()));
				break;
			case STOP_MARKET:
				fill = ((openOrder.isBuy() && lastTick.getMidPrice() >= openOrder.getStopPrice()) || (openOrder.isSell() && lastTick.getMidPrice() <= openOrder
						.getStopPrice()));
				break;
			case STOP_LIMIT:
				fill = ((openOrder.isBuy() && lastTick.getMidPrice() >= openOrder.getStopPrice()) || (openOrder.isSell() && lastTick.getMidPrice() <= openOrder
						.getStopPrice()));
				fill &= ((openOrder.isBuy() && lastTick.getAsk() <= openOrder.getPrice()) || (openOrder.isSell() && lastTick.getBid() >= openOrder.getPrice()));
				break;
			case TRAIL_MARKET:
				openOrder.updateMinMaxPrice(lastTick.getMidPrice());
				fill = ((openOrder.isBuy() && lastTick.getMidPrice() >= openOrder.getMinPrice() + openOrder.getTrailStopOffset()) || (openOrder.isSell() && lastTick
						.getMidPrice() <= openOrder.getMaxPrice() - openOrder.getTrailStopOffset()));
				break;
			case TRAIL_LIMIT:
				openOrder.updateMinMaxPrice(lastTick.getMidPrice());
				fill = ((openOrder.isBuy() && lastTick.getMidPrice() >= openOrder.getMinPrice() + openOrder.getTrailStopOffset()) || (openOrder.isSell() && lastTick
						.getMidPrice() <= openOrder.getMaxPrice() - openOrder.getTrailStopOffset()));
				fill &= ((openOrder.isBuy() && lastTick.getAsk() <= openOrder.getPrice()) || (openOrder.isSell() && lastTick.getBid() >= openOrder.getPrice()));
				break;
			}

			// System.out.println(openOrder);
			// System.out.println(lastTick.getMidPrice());
			// System.out.println(fill);

			if (fill) {
				double fillPrice = openOrder.isBuy() ? lastTick.getAsk() : lastTick.getBid();
				int fillQuantity = 0;
				if (openOrder.isBuy()) {
					fillQuantity = Math.min(lastTick.getAskSize(), openOrder.getQuantity());
				} else {
					fillQuantity = Math.max(-lastTick.getBidSize(), openOrder.getQuantity());
				}
				if (fillQuantity == 0) {
					fillQuantity = openOrder.getQuantity();
				}
				openOrder.update(fillQuantity, fillPrice, dt);

				if (VERBOSE.get()) {
					logExecution(openOrder, fillQuantity);
				}

				if (openOrder.isFilled()) {
					DateTime fillDate = openOrder.getFillDate();
					double commissionAmount = commission.calculate(openOrder.getQuantityFilled(), openOrder.getAvgFillPrice());
					openOrder.setCommission(commissionAmount);
					Position position = portfolio.getPosition(symbol);
					double[] values = position.update(fillDate, fillQuantity, fillPrice, commissionAmount);
					if (values[0] != 0.0) {
						portfolio.addCash(symbol.getCurrency(), values[3]);
						performanceTracker.updateTrades(fillDate, symbol, -(int)values[0], values[1], values[2], values[3]);
					}
					
					if (VERBOSE.get()) {
						logger.info("Filled {}", openOrder);
						logTrade(openOrder, position.getQuantity(), values[0], values[3], values[4]);
					}

					for (OrderListener listener : orderListeners) {
						try {
							listener.onOrderFilled(openOrder, position);
						} catch (Throwable t) {
							logger.error(t.getMessage(), t);
						}
					}
				}
			}
		}
		lastProcessOrder = dt;
	}

	protected void logExecution(OpenOrder openOrder, int quantity) {
		Object[] params = new Object[] { 
				openOrder.getFillDate(), 
				"EXEC", 
				openOrder.getAction(), 
				openOrder.getType(), 
				quantity, 
				openOrder.getSymbol(),
				openOrder.getSymbol().getCurrency(), 
				Util.round(openOrder.getLastFillPrice(), 4), 
				"", 
				"", 
				"", 
				"", 
				"",
				openOrder.getReference() != null ? openOrder.getReference() : "", "DU000000" };
		blotter.info(MarkerFactory.getMarker("EXECUTION"), "{},{},{},{},{},{},{},{},{},{},{},{},{},{}", params);
	}

	protected void logTrade(OpenOrder openOrder, int position, double costBasis, double realized, double unrealized) {
		Object[] params = new Object[] { 
				openOrder.getFillDate(), 
				"TRADE", 
				openOrder.getAction(), 
				openOrder.getType(), 
				openOrder.getQuantityFilled(),
				openOrder.getSymbol(), 
				openOrder.getSymbol().getCurrency(), 
				Util.round(openOrder.getAvgFillPrice(), 4), 
				position, 
				Util.round(costBasis, 4),
				Util.round(realized, 4), 
				Util.round(unrealized, 4), 
				Util.round(openOrder.getCommission(), 4),
				openOrder.getReference() != null ? openOrder.getReference() : "", "DU000000" };
		blotter.info(MarkerFactory.getMarker("TRADE"), "{},{},{},{},{},{},{},{},{},{},{},{},{},{}", params);
	}
	
	protected void processPerformanceTracker(DateTime dateTime) {
		performanceTracker.updatePortfolio(dateTime, portfolio);
	}
	
	@Override
	public void onDay(DateTime dateTime) {
	}

	@Override
	public void onHour(DateTime dateTime) {
		processPerformanceTracker(dateTime);
	}

	@Override
	public void onMinute(DateTime dateTime) {
	}

	@Override
	public void onBar(Bar bar) {
		processOpenOrders(bar.getDateTime());
	}
	
	@Override
	public void onTick(Tick tick) {
		processOpenOrders(tick.getDateTime());
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
	public Commission getCommission() {
		return commission;
	}
	
	@Override
	public void setCommission(Commission commission) {
		this.commission = commission;
	}
}
