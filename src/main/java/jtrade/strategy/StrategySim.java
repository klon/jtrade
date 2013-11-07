package jtrade.strategy;

import java.io.File;

import jtrade.Recorder;
import jtrade.Symbol;
import jtrade.marketfeed.BarFileMarketFeed;
import jtrade.marketfeed.TickFileMarketFeed;
import jtrade.trader.DummyTrader;
import jtrade.util.Configurable;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrategySim implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(StrategySim.class);
	private Strategy strategy;
	private boolean cancel;
	private boolean done;

	public StrategySim(Strategy strategy, File dataDir, DateTime fromDate, DateTime toDate, boolean useTickData, int barSizeSeconds, Symbol... symbols) {
		this.strategy = strategy;
		if (barSizeSeconds <= 0) {
			this.strategy.setMarketFeed(new TickFileMarketFeed(dataDir, fromDate, toDate, symbols));
		} else {
			this.strategy.setMarketFeed(new BarFileMarketFeed(dataDir, fromDate, toDate, useTickData, barSizeSeconds, symbols));
		}
		strategy.setTrader(new DummyTrader(strategy.getMarketFeed()));
		strategy.setRecorder(new Recorder() {
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

	public boolean isDone() {
		return done;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void cancel() {
		cancel = true;
	}

	@Override
	public void run() {
		try {
			logger.info("Initializing {} with config {}", strategy.getClass().getSimpleName(), Configurable.getConfiguration(strategy));
			strategy.init();
			strategy.getTrader().connect();
			strategy.getMarketFeed().connect();
			while (strategy.getMarketFeed().isConnected() && strategy.getTrader().isConnected() && !cancel) {
				Thread.sleep(100);
			}
			strategy.destroy();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			done = true;
			strategy.getTrader().disconnect();
			strategy.getMarketFeed().disconnect();
		}
	}

}
