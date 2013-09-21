package jtrade.strategy;

import java.io.File;

import jtrade.Recorder;
import jtrade.Symbol;
import jtrade.marketfeed.BarFileMarketFeed;
import jtrade.marketfeed.MarketFeed;
import jtrade.marketfeed.TickFileMarketFeed;
import jtrade.trader.DummyTrader;
import jtrade.trader.Trader;
import jtrade.util.Configurable;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrategySim implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(StrategySim.class);

	private MarketFeed marketFeed;
	private Trader trader;
	private Strategy strategy;
	private boolean cancel;
	private boolean done;

	public StrategySim(Strategy strategy, File dataDir, DateTime fromDate, DateTime toDate, boolean useTickData, int barSizeSeconds, Symbol... symbols) {
		this.marketFeed = useTickData && barSizeSeconds <= 0 ? new TickFileMarketFeed(dataDir, fromDate, toDate, symbols) : new BarFileMarketFeed(dataDir,
				fromDate, toDate, useTickData, barSizeSeconds, symbols);
		this.strategy = strategy;
		this.trader = new DummyTrader(marketFeed, true);
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
			strategy.setMarketFeed(marketFeed);
			strategy.setTrader(trader);
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
			strategy.init();
			trader.connect();
			marketFeed.connect();

			while (marketFeed.isConnected() && trader.isConnected() && !cancel) {
				Thread.sleep(100);
			}

			strategy.destroy();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			done = true;
			marketFeed.disconnect();
			trader.disconnect();
		}
	}

}
