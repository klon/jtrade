package jtrade.test.unit;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jtrade.Symbol;
import jtrade.marketfeed.MarketListener;
import jtrade.marketfeed.Tick;
import jtrade.marketfeed.TickFileMarketFeed;
import jtrade.marketfeed.TickListener;
import jtrade.test.TestDurationWrapper;
import jtrade.util.Configurable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ TestDurationWrapper.class })
public class TestTickFileMarketFeed {
	Symbol symbol1;
	Symbol symbol2;
	DateTime fromDate;
	DateTime toDate;
	List<Tick> expectedTicks;
	List<DateTime> expectedDates;

	@BeforeClass
	public void setUp() {
		symbol1 = new Symbol("A-NYSE-USD-STOCK");
		symbol2 = new Symbol("B-NYSE-USD-STOCK");
		fromDate = new DateTime(2013, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
		toDate = new DateTime(2013, 1, 3, 0, 0, 0, 0, DateTimeZone.UTC);

		DateTime date = new DateTime(2013, 1, 1, 23, 59, 59, 0, DateTimeZone.UTC);
		expectedDates = new ArrayList<DateTime>();
		expectedDates.add(date.plusSeconds(1));
		expectedDates.add(date.plusSeconds(1));
		
		expectedTicks = new ArrayList<Tick>();
		for (int i = 0; i < 10; i++) {
			expectedTicks.add(new Tick(symbol1, date, 3.0 + i, 12 + i, 2.0 + i, 13 + i, 2.5 + i, 14 + i, 10 * (i + 1), null));
			expectedTicks.add(new Tick(symbol2, date, 3.0 + i, 12 + i, 2.0 + i, 13 + i, 2.5 + i, 14 + i, 10 * (i + 1), null));
			date = date.plus(400);
		}
		Configurable.configure("jtrade.marketfeed.AbstractMarketFeed#DATA_DIR", new File("./test/fixture"));
	}

	@Test()
	public void testTickFileMarketFeed() {
		final List<DateTime> dates = new ArrayList<DateTime>();
		final List<Tick> ticks = new ArrayList<Tick>();
		TickFileMarketFeed marketFeed = new TickFileMarketFeed();
		marketFeed.reset(fromDate, toDate, symbol1, symbol2);
		marketFeed.connect();
		assertEquals(marketFeed.isConnected(), true);
		marketFeed.disconnect();
		assertEquals(marketFeed.isConnected(), false);

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

		marketFeed.addTickListener(symbol1, new TickListener() {
			@Override
			public void onTick(Tick tick) {
				ticks.add(tick);
			}
		});

		marketFeed.addTickListener(symbol2, new TickListener() {
			@Override
			public void onTick(Tick tick) {
				ticks.add(tick);
			}
		});

		marketFeed.connect();
		assertEquals(marketFeed.isConnected(), true);
		try {
			while (marketFeed.isConnected()) {
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
		}
		marketFeed.removeAllListeners();

		for (int i = 0; i < expectedDates.size(); i++) {

			assertEquals(expectedDates.get(i), dates.get(i));
		}
		for (int i = 0; i < expectedTicks.size(); i++) {
			assertEquals(expectedTicks.get(i), ticks.get(i));
		}
	}

}
