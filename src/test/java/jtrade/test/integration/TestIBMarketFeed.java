package jtrade.test.integration;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jtrade.Symbol;
import jtrade.SymbolFactory;
import jtrade.marketfeed.Bar;
import jtrade.marketfeed.BarListener;
import jtrade.marketfeed.IBMarketFeed;
import jtrade.marketfeed.MarketListener;
import jtrade.marketfeed.Tick;
import jtrade.marketfeed.TickListener;
import jtrade.test.TestDurationWrapper;
import jtrade.util.Configurable;

import org.joda.time.DateTime;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ TestDurationWrapper.class })
public class TestIBMarketFeed {
	File dataDir;
	Symbol symbol;
	int barSizeSeconds;

	@BeforeClass
	public void setUp() {
		dataDir = new File("./test/ib");
		//symbol = new Symbol("EUR-IDEALPRO-USD-CASH");
		//symbol = new Symbol("COMP-NASDAQ-USD-INDEX");
		//symbol = new Symbol("AAPL-SMART-USD-STOCK");
		symbol = SymbolFactory.getESFutureSymbol(new DateTime());
		barSizeSeconds = 60;
		
		Configurable.configure("jtrade.marketfeed.AbstractMarketFeed#DATA_DIR", dataDir);
		Configurable.configure("jtrade.marketfeed.IBMarketFeed#SERVER_HOSTS", "localhost:4000");
		Configurable.configure("jtrade.marketfeed.IBMarketFeed#CLIENT_ID", 99999);
	}
	
	@Test()
	public void testIBMarketFeedTickListener() throws Exception {
		final List<Tick> ticks = new ArrayList<Tick>();
		IBMarketFeed marketFeed = new IBMarketFeed();
		try {
			marketFeed.connect();
			marketFeed.addTickListener(symbol, new TickListener() {
				@Override
				public void onTick(Tick tick) {
					ticks.add(tick);
				}
			}, false, null);
			
			marketFeed.addTickListener(new TickListener() {
				@Override
				public void onTick(Tick tick) {
					ticks.add(tick);
				}
			});

			assertEquals(marketFeed.isConnected(), true);
			long start = System.currentTimeMillis();
			try {
				while (marketFeed.isConnected() && ticks.isEmpty()) {
					Thread.sleep(10);
					if (System.currentTimeMillis() - start > 70000) {
						throw new SkipException("Test timed out waiting for marketdata");
					}
				}
			} catch (InterruptedException e) {
			}
			assertEquals(ticks.get(0), ticks.get(1));
			assertEquals(ticks.get(ticks.size() - 1), marketFeed.getLastTick(symbol));
			assertEquals(symbol, ticks.get(0).getSymbol());
			assertTrue(ticks.get(0).getAsk() > 0);
			assertTrue(ticks.get(0).getAskSize() > 0);
			assertTrue(ticks.get(0).getBid() > 0);
			assertTrue(ticks.get(0).getBidSize() > 0);
			
			marketFeed.removeAllListeners();
			
		} finally {
			marketFeed.disconnect();
		}
	}

	@Test()
	public void testIBMarketFeedBarListener() throws Exception {
		final List<Bar> bars = new ArrayList<Bar>();
		IBMarketFeed marketFeed = new IBMarketFeed();
		try {
			marketFeed.connect();
			marketFeed.addBarListener(symbol, new BarListener() {
				@Override
				public void onBar(Bar bar) {
					bars.add(bar);
				}
			}, barSizeSeconds, null);
			
			marketFeed.addBarListener(new BarListener() {
				@Override
				public void onBar(Bar bar) {
					bars.add(bar);
				}
			});

			assertEquals(marketFeed.isConnected(), true);
			long start = System.currentTimeMillis();
			try {
				while (marketFeed.isConnected() && bars.isEmpty()) {
					Thread.sleep(10);
					if (System.currentTimeMillis() - start > 70000) {
						throw new SkipException("Test timed out waiting for marketdata");
					}
				}
			} catch (InterruptedException e) {
			}
			assertEquals(2, bars.size());
			assertEquals(bars.get(0), bars.get(1));
			assertEquals(bars.get(0), marketFeed.getLastBar(symbol));
			assertEquals(symbol, bars.get(0).getSymbol());
			assertEquals(barSizeSeconds, bars.get(0).getBarSize().getStandardSeconds());
			assertTrue(bars.get(0).getOpen() > 0);
			assertTrue(bars.get(0).getHigh() > 0);
			assertTrue(bars.get(0).getLow() > 0);
			assertTrue(bars.get(0).getClose() > 0);
			
			marketFeed.removeAllListeners();
			
		} finally {
			marketFeed.disconnect();
		}
	}

	@Test()
	public void testIBMarketFeedMarketListener() throws Exception {
		DateTime now = new DateTime();
		final List<DateTime> dates = new ArrayList<DateTime>();
		IBMarketFeed marketFeed = new IBMarketFeed();
		try {	
			assertEquals(marketFeed.isConnected(), false);
			assertEquals(dataDir.getCanonicalPath(), marketFeed.getDataDir().getCanonicalPath());
			marketFeed.connect();
			marketFeed.disconnect();
			assertEquals(marketFeed.isConnected(), false);
			marketFeed.connect();
			
			marketFeed.addMarketListener(new MarketListener() {
				@Override
				public void onMinute(DateTime dateTime) {
					dates.add(dateTime);
				}

				@Override
				public void onHour(DateTime dateTime) {
					dates.add(dateTime);
				}

				@Override
				public void onDay(DateTime dateTime) {
					dates.add(dateTime);
				}
			});

			assertEquals(marketFeed.isConnected(), true);
			try {
				while (marketFeed.isConnected() && dates.size() >= 2) {
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
			}
			assertEquals(now.getDayOfYear(), dates.get(0).getDayOfYear());
			assertEquals(now.getMinuteOfDay(), dates.get(0).getMinuteOfDay());
			assertEquals(now.getMinuteOfDay() + 1, dates.get(1).getMinuteOfDay());
			marketFeed.removeAllListeners();
			
		} catch (Exception e) {
			marketFeed.disconnect();
		}
	}
}
