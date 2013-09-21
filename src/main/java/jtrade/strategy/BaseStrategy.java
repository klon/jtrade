package jtrade.strategy;

import jtrade.Recorder;
import jtrade.Symbol;
import jtrade.marketfeed.IBMarketFeed;
import jtrade.marketfeed.MarketFeed;
import jtrade.marketfeed.MarketListener;
import jtrade.trader.OpenOrder;
import jtrade.trader.OrderListener;
import jtrade.trader.Position;
import jtrade.trader.Trader;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseStrategy implements Strategy, MarketListener, OrderListener {
	protected Logger logger;
	protected MarketFeed marketFeed;
	protected Trader trader;
	protected Recorder recorder;
	protected boolean active;
	protected boolean verbose;

	protected BaseStrategy() {
		this.active = true;
		this.verbose = false;
		this.logger = LoggerFactory.getLogger(this.getClass());
		setRecorder(new Recorder() {
			@Override
			public void record(String name, DateTime dateTime, boolean autoScale, double... value) {
			}

			@Override
			public void record(String name, DateTime dateTime, double... value) {
			}

			@Override
			public void plot() {
			}
		});
	}

	@Override
	public void destroy() {
		if (trader != null && trader.isConnected()) {
			trader.removeOrderListener(this);
		}
		if (marketFeed != null && marketFeed.isConnected()) {
			marketFeed.removeListener(this);
		}
	}
	
	public Position setPosition(Symbol symbol, int quantity) {
		Position position = trader.getPortfolio().getPosition(symbol);
		if (position.getQuantity() == quantity) {
			return position;
		}
		trader.cancelOrder(symbol, null);
		trader.placeOrder(symbol, quantity - position.getQuantity(), getClass().getSimpleName());
		return null;
	}

	@Override
	public MarketFeed getMarketFeed() {
		return marketFeed;
	}

	@Override
	public void setMarketFeed(MarketFeed marketFeed) {
		this.marketFeed = marketFeed;
		if (marketFeed instanceof IBMarketFeed) {
			verbose = true;
		}
	}

	@Override
	public Trader getTrader() {
		return trader;
	}

	@Override
	public void setTrader(Trader trader) {
		this.trader = trader;
	}

	@Override
	public Recorder getRecorder() {
		return recorder;
	}

	@Override
	public void setRecorder(Recorder recorder) {
		this.recorder = recorder;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public void onDay(DateTime dateTime) {
	}

	@Override
	public void onHour(DateTime dateTime) {
	}

	@Override
	public void onMinute(DateTime dateTime) {
	}

	@Override
	public void onOrderPlaced(OpenOrder openOrder) {
	}

	@Override
	public void onOrderFilled(OpenOrder openOrder, Position position) {
	}

	@Override
	public void onOrderFailed(OpenOrder openOrder) {
	}

	@Override
	public void onOrderCancelled(OpenOrder openOrder) {
	}

	public void logError(String message) {
		logger.error(message);
	}

	public void logError(String message, Object arg1) {
		logger.error(message, arg1);
	}

	public void logError(String message, Object arg1, Object arg2) {
		logger.error(message, arg1, arg2);
	}

	public void logError(String message, Object... args) {
		logger.error(message, args);
	}

	public void logWarn(String message) {
		logger.warn(message);
	}

	public void logWarn(String message, Object arg1) {
		logger.warn(message, arg1);
	}

	public void logWarn(String message, Object arg1, Object arg2) {
		logger.warn(message, arg1, arg2);
	}

	public void logWarn(String message, Object... args) {
		logger.warn(message, args);
	}

	public void logInfo(String message) {
		if (verbose) {
			logger.info(message);
		}
	}

	public void logInfo(String message, Object arg1) {
		if (verbose) {
			logger.info(message, arg1);
		}
	}

	public void logInfo(String message, Object arg1, Object arg2) {
		if (verbose) {
			logger.info(message, arg1, arg2);
		}
	}

	public void logInfo(String message, Object... args) {
		if (verbose) {
			logger.info(message, args);
		}
	}

	public void logDebug(String message) {
		if (verbose) {
			logger.debug(message);
		}
	}

	public void logDebug(String message, Object arg1) {
		if (verbose) {
			logger.debug(message, arg1);
		}
	}

	public void logDebug(String message, Object arg1, Object arg2) {
		if (verbose) {
			logger.debug(message, arg1, arg2);
		}
	}

	public void logDebug(String message, Object... args) {
		if (verbose) {
			logger.debug(message, args);
		}
	}

	@Override
	public String toString() {
		return Util.toString(this).concat(" ").concat(super.toString());
	}
}
