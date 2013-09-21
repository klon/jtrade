package jtrade.test;

import java.io.File;
import java.util.NavigableMap;
import java.util.TreeMap;

import jtrade.Symbol;
import jtrade.marketfeed.Bar;
import jtrade.marketfeed.BarListener;
import jtrade.marketfeed.Cleaner;
import jtrade.marketfeed.MarketFeed;
import jtrade.marketfeed.MarketListener;
import jtrade.marketfeed.Tick;
import jtrade.marketfeed.TickListener;
import jtrade.timeseries.TimeSeries;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class MockBarMarketFeed implements MarketFeed {
	NavigableMap<DateTime, Bar> bars;
	int barSizeSeconds;
	DateTime now;
	boolean connected;

	public MockBarMarketFeed(Symbol symbol, int barSizeSeconds, DateTime date, double[] opens, double[] highs, double[] lows, double[] closes, double[] waps,
			int[] volumes, int[] trades) {
		this.bars = new TreeMap<DateTime, Bar>();
		for (int i = 0; i < opens.length; i++) {
			Bar b = new Bar(new Duration(barSizeSeconds * 1000), symbol, date, opens[i], highs[i], lows[i], closes[i], waps[i], volumes[i], trades[i]);
			this.bars.put(b.getDateTime(), b);
		}
		this.barSizeSeconds = barSizeSeconds;
		now = date;
	}

	public MockBarMarketFeed(Bar[] bars) {
		this.bars = new TreeMap<DateTime, Bar>();
		for (Bar b : bars) {
			this.bars.put(b.getDateTime(), b);
		}
		now = this.bars.firstKey();
	}

	@Override
	public void connect() {
		connected = true;
	}

	@Override
	public void disconnect() {
		connected = false;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public File getDataDir() {
		return new File(System.getProperty("user.home"), "testdata");
	}

	@Override
	public Tick getLastTick(Symbol symbol) {
		return getLastTick(symbol, now);
	}

	@Override
	public Tick getLastTick(Symbol symbol, DateTime date) {
		Bar bar = getLastBar(symbol, date, barSizeSeconds);
		if (bar == null) {
			return null;
		}
		Tick tick = new Tick(symbol, bar.getDateTime(), bar.getClose() + bar.getSymbol().getMinTick() * 1.0, 0, bar.getClose() - bar.getSymbol().getMinTick()
				* 1.0, 1, bar.getClose(), bar.getTrades() > 0 ? 1 : 0, 0, null);
		return tick;
	}

	@Override
	public Bar getLastBar(Symbol symbol) {
		Bar b = bars.floorEntry(now).getValue();
		if (!b.getSymbol().equals(symbol)) {
			throw new IllegalArgumentException("Invalid symbol: " + symbol);
		}
		return b;
	}

	@Override
	public Bar getLastBar(Symbol symbol, DateTime date, int barSizeSeconds) {
		Bar b = bars.floorEntry(date).getValue();
		if (!b.getSymbol().equals(symbol)) {
			throw new IllegalArgumentException("Invalid symbol: " + symbol);
		}
		if (b.getBarSize().getStandardSeconds() != barSizeSeconds) {
			throw new IllegalArgumentException("Invalid barSizeSeconds: " + barSizeSeconds);
		}
		return b;
	}

	@Override
	public NavigableMap<DateTime, Bar> getBarData(Symbol symbol, DateTime fromDate, DateTime toDate, int barSizeSeconds) {
		return null;
	}

	@Override
	public NavigableMap<DateTime, Tick> getTickData(Symbol symbol, DateTime fromDate, DateTime toDate) {
		return null;
	}

	@Override
	public TimeSeries getTimeSeries(Symbol symbol, DateTime fromDate, DateTime toDate, int barSizeSeconds, String attribute) {
		return null;
	}

	@Override
	public void addTickListener(TickListener listener) {

	}

	@Override
	public void addTickListener(Symbol symbol, TickListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addTickListener(Symbol symbol, TickListener listener, boolean marketDepth, Cleaner cleaner) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeTickListener(TickListener listener) {

	}

	@Override
	public void removeTickListener(Symbol symbol, TickListener listener) {

	}

	@Override
	public void addBarListener(BarListener listener) {

	}

	@Override
	public void addBarListener(Symbol symbol, BarListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addBarListener(Symbol symbol, BarListener listener, int barSizeSeconds, Cleaner cleaner) {

	}

	@Override
	public void removeBarListener(BarListener listener) {

	}

	@Override
	public void removeBarListener(Symbol symbol, BarListener listener) {

	}

	@Override
	public void addMarketListener(MarketListener listener) {

	}

	@Override
	public void removeMarketListener(MarketListener listener) {

	}

	@Override
	public void removeListener(Object listener) {

	}

	@Override
	public void removeAllListeners() {

	}

}
