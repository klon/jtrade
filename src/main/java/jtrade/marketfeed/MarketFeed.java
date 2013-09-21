package jtrade.marketfeed;

import java.io.File;
import java.util.NavigableMap;

import jtrade.Symbol;
import jtrade.timeseries.TimeSeries;

import org.joda.time.DateTime;

public interface MarketFeed {

	public void connect();

	public void disconnect();

	public boolean isConnected();

	public File getDataDir();

	public Tick getLastTick(Symbol symbol);

	public Tick getLastTick(Symbol symbol, DateTime date);

	public Bar getLastBar(Symbol symbol);

	public Bar getLastBar(Symbol symbol, DateTime date, int barSizeSeconds);

	public NavigableMap<DateTime, Bar> getBarData(Symbol symbol, DateTime fromDate, DateTime toDate, int barSizeSeconds);

	public NavigableMap<DateTime, Tick> getTickData(Symbol symbol, DateTime fromDate, DateTime toDate);

	public TimeSeries getTimeSeries(Symbol symbol, DateTime fromDate, DateTime toDate, int barSizeSeconds, String attribute);

	public void addTickListener(TickListener listener);

	public void addTickListener(Symbol symbol, TickListener listener);

	public void addTickListener(Symbol symbol, TickListener listener, boolean marketDepth, Cleaner cleaner);

	public void removeTickListener(TickListener listener);

	public void removeTickListener(Symbol symbol, TickListener listener);

	public void addBarListener(BarListener listener);

	public void addBarListener(Symbol symbol, BarListener listener);

	public void addBarListener(Symbol symbol, BarListener listener, int barSizeSeconds, Cleaner cleaner);

	public void removeBarListener(BarListener listener);

	public void removeBarListener(Symbol symbol, BarListener listener);

	public void addMarketListener(MarketListener listener);

	public void removeMarketListener(MarketListener listener);

	public void removeListener(Object listener);

	public void removeAllListeners();
}
