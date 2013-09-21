package jtrade.test.unit;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableMap;

import jtrade.Symbol;
import jtrade.marketfeed.Bar;
import jtrade.marketfeed.BarFileMarketFeed;
import jtrade.marketfeed.BarListener;
import jtrade.marketfeed.MarketListener;
import jtrade.test.TestDurationWrapper;
import jtrade.timeseries.TimeSeries;
import jtrade.util.Configurable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ TestDurationWrapper.class })
public class TestBarFileMarketFeed {
	Symbol symbol1;
	Symbol symbol2;
	DateTime fromDate;
	DateTime toDate;
	int barSizeSeconds;
	List<Bar> expectedBars;
	List<DateTime> expectedDates;

	@BeforeClass
	public void setUp() {
		symbol1 = new Symbol("A-NYSE-USD-STOCK");
		symbol2 = new Symbol("B-NYSE-USD-STOCK");
		fromDate = new DateTime(2013, 1, 1, 0, 0, 0, 0, DateTimeZone.UTC);
		toDate = new DateTime(2013, 1, 3, 0, 0, 0, 0, DateTimeZone.UTC);
		barSizeSeconds = 60;

		Duration barSize = new Duration(barSizeSeconds * 1000);
		DateTime date = new DateTime(2013, 1, 1, 23, 59, 0, 0, DateTimeZone.UTC);
		expectedDates = new ArrayList<DateTime>();
		expectedBars = new ArrayList<Bar>();
		for (int i = 0; i < 10; i++) {
			expectedDates.add(date);
			if (date.getMinuteOfHour() == 0) {
				expectedDates.add(date);
				if (date.getHourOfDay() == 0) {
					expectedDates.add(date);
				}
			}
			expectedBars.add(new Bar(barSize, symbol1, date, 2.0 + i, 3.0 + i, 1.0 + i, 2.5 + i, 2.2 + i, 10 * (i + 1), 1));
			expectedBars.add(new Bar(barSize, symbol2, date, 2.0 + i, 3.0 + i, 1.0 + i, 2.5 + i, 2.2 + i, 10 * (i + 1), 1));
			date = date.plusMinutes(1);
		}
		Configurable.configure("jtrade.marketfeed.AbstractMarketFeed#DATA_DIR", new File("./test/fixture"));
		Configurable.configure("jtrade.marketfeed.BarFileMarketFeed#USE_TICK_DATA", false);
		Configurable.configure("jtrade.marketfeed.BarFileMarketFeed#BAR_SIZE", barSizeSeconds);
	}

	@Test()
	public void testBarFileMarketFeed() {
		final List<DateTime> dates = new ArrayList<DateTime>();
		final List<Bar> bars = new ArrayList<Bar>();
		BarFileMarketFeed marketFeed = new BarFileMarketFeed();
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

		marketFeed.addBarListener(symbol1, new BarListener() {
			@Override
			public void onBar(Bar bar) {
				bars.add(bar);
			}
		});

		marketFeed.addBarListener(symbol2, new BarListener() {
			@Override
			public void onBar(Bar bar) {
				bars.add(bar);
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
		for (int i = 0; i < expectedBars.size(); i++) {
			assertEquals(expectedBars.get(i), bars.get(i));
		}
	}

	@Test()
	public void testBarFileMarketFeedHistoricalData() {
		BarFileMarketFeed marketFeed = new BarFileMarketFeed();
		NavigableMap<DateTime, Bar> barData = marketFeed.getBarData(symbol1, fromDate, toDate, barSizeSeconds);
		
		Bar bar = marketFeed.getLastBar(symbol2, expectedDates.get(expectedDates.size() - 1), 60);
		assertEquals(expectedDates.get(expectedDates.size() - 1), bar.getDateTime());
		assertEquals(expectedBars.get(expectedBars.size() - 1), bar);
		
		
		List<Bar> uniqueExpectedBars = new ArrayList<Bar>(new LinkedHashSet<Bar>(expectedBars));
		for (Iterator<Bar> iter = uniqueExpectedBars.iterator(); iter.hasNext();) {
			if (!iter.next().getSymbol().equals(symbol1)) {
				iter.remove();
			}
		}
		List<DateTime> dates = new ArrayList<DateTime>(barData.keySet());
		List<Bar> bars = new ArrayList<Bar>(barData.values());
		for (int i = 0; i < bars.size(); i++) {
			assertEquals(uniqueExpectedBars.get(i), bars.get(i));
			assertEquals(dates.get(i), bars.get(i).getDateTime());
		}
		
		TimeSeries ts1 = marketFeed.getTimeSeries(symbol2, fromDate, toDate, barSizeSeconds, "close");
		for (int i = 0; i < bars.size(); i++) {
			assertEquals(uniqueExpectedBars.get(i).getDateTime(), ts1.dates()[i]);
			assertEquals(uniqueExpectedBars.get(i).getClose(), ts1.data()[i]);
		}
		
		TimeSeries ts2 = marketFeed.getTimeSeries(symbol2, fromDate, toDate, barSizeSeconds * 2, "close");
		for (int i = 0; i < ts2.size() && i * 2 < bars.size(); i++) {
			assertEquals(uniqueExpectedBars.get(i * 2).getClose(), ts2.data()[i]);
		}
	}
		
}
