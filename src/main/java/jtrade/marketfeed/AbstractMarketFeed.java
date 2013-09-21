package jtrade.marketfeed;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import jtrade.JTradeException;
import jtrade.Symbol;
import jtrade.SymbolFactory;
import jtrade.io.MarketDataIO;
import jtrade.timeseries.TimeSeries;
import jtrade.timeseries.TimeSeriesArray;
import jtrade.util.Configurable;
import jtrade.util.Util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMarketFeed implements MarketFeed {
	private static final Logger logger = LoggerFactory.getLogger(AbstractMarketFeed.class);
	public final Configurable<File> DATA_DIR = new Configurable<File>("DATA_DIR", new File("./marketdata"));

	protected File dataDir;
	protected List<MarketListener> marketListeners;

	protected AbstractMarketFeed() {
		this(null);
	}

	protected AbstractMarketFeed(File dataDir) {
		if (dataDir == null) {
			dataDir = DATA_DIR.get();
		}
		if (dataDir.getPath().startsWith("~")) {
			dataDir = new File(dataDir.getPath().replace("~", System.getProperty("user.home")));
			logger.warn("{}", dataDir);
		}
		try {
			dataDir = dataDir.getCanonicalFile();
		} catch (IOException e) {
			throw new IllegalArgumentException("Invalid dataDir: " + dataDir);
		}
		if (!dataDir.exists() && !dataDir.mkdirs()) {
			throw new IllegalArgumentException("Invalid dataDir: " + dataDir);
		}
		this.dataDir = dataDir;
		this.marketListeners = new LinkedList<MarketListener>();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				disconnect();
			}
		});
	}

	@Override
	public File getDataDir() {
		return dataDir;
	}

	@Override
	public synchronized void addMarketListener(MarketListener listener) {
		if (!marketListeners.contains(listener)) {
			marketListeners.add(listener);
		}
	}

	@Override
	public synchronized void removeMarketListener(MarketListener listener) {
		marketListeners.remove(listener);
	}

	protected void fireDayEvent(DateTime dateTime) {
		for (MarketListener listener : marketListeners) {
			try {
				listener.onDay(dateTime);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
		}
	}

	protected void fireHourEvent(DateTime dateTime) {
		for (MarketListener listener : marketListeners) {
			try {
				listener.onHour(dateTime);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
		}
	}

	protected void fireMinuteEvent(DateTime dateTime) {
		for (MarketListener listener : marketListeners) {
			try {
				listener.onMinute(dateTime);
			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			}
		}
	}

	@Override
	public Tick getLastTick(Symbol symbol, DateTime date) {
		NavigableMap<DateTime, Tick> ticks = getTickData(symbol, date.minusDays(7), date);
		if (ticks.isEmpty()) {
			return null;
		}
		return ticks.get(ticks.lastKey());
	}

	@Override
	public Bar getLastBar(Symbol symbol, DateTime date, int barSizeSeconds) {
		NavigableMap<DateTime, Bar> bars = getBarData(symbol, date.minusDays(7), date, barSizeSeconds);
		if (bars.isEmpty()) {
			return null;
		}
		return bars.get(bars.lastKey());
	}

	public TimeSeries getTimeSeries(String symbol, String fromDate, String toDate, int barSizeSeconds, String attribute) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
		return getTimeSeries(SymbolFactory.getSymbol(symbol), formatter.parseDateTime(fromDate), formatter.parseDateTime(toDate).withTime(23, 59, 59, 999),
				barSizeSeconds, attribute);
	}

	@Override
	public TimeSeries getTimeSeries(Symbol symbol, DateTime fromDate, DateTime toDate, int barSizeSeconds, String attribute) {
		if (barSizeSeconds <= 0) {
			NavigableMap<DateTime, Tick> ticks = getTickData(symbol, fromDate, toDate);
			if (ticks.isEmpty()) {
				return new TimeSeriesArray();
			}
			DateTime[] dates = new DateTime[ticks.size()];
			double[] data = new double[ticks.size()];
			int i = 0;
			for (Tick t : ticks.values()) {
				dates[i] = t.getDateTime();
				try {
					data[i] = Util.getDoubleProperty(t, attribute);
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
				i++;
			}
			return new TimeSeriesArray(dates, data);
		}
		NavigableMap<DateTime, Bar> bars = getBarData(symbol, fromDate, toDate, barSizeSeconds);
		if (bars.isEmpty()) {
			return new TimeSeriesArray();
		}
		DateTime[] dates = new DateTime[bars.size()];
		double[] data = new double[bars.size()];
		int i = 0;
		for (Bar b : bars.values()) {
			dates[i] = b.getDateTime();
			try {
				data[i] = Util.getDoubleProperty(b, attribute);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
			i++;
		}
		return new TimeSeriesArray(dates, data);
	}

	@Override
	public NavigableMap<DateTime, Bar> getBarData(Symbol symbol, DateTime fromDate, DateTime toDate, int barSizeSeconds) {
		NavigableMap<DateTime, Bar> result = new TreeMap<DateTime, Bar>();
		DateTime month = fromDate.withDayOfMonth(1).withTime(0, 0, 0, 0);

		int sourceBarSizeSeconds = 0;
		int[] barSeconds = new int[] { 1, 5, 60, 3600, 86400 };
		for (int i = 0; i < barSeconds.length && sourceBarSizeSeconds < barSizeSeconds; i++) {
			File f = MarketDataIO.createReadFile(symbol, month, barSeconds[i], dataDir);
			if (f.exists()) {
				sourceBarSizeSeconds = barSeconds[i];
			}
		}
		if (sourceBarSizeSeconds == 0) {
			sourceBarSizeSeconds = 60;
		}

		while (toDate.isAfter(month)) {
			if (symbol.getExpiry() != null && month.isAfter(symbol.getExpiry())) {
				break;
			}
			DateTime nextMonth = month.plusMonths(1);
			File file = MarketDataIO.createReadFile(symbol, month, sourceBarSizeSeconds, dataDir);
			File nextFile = MarketDataIO.createReadFile(symbol, nextMonth, sourceBarSizeSeconds, dataDir);
			NavigableMap<DateTime, Bar> bars = readHistoricalDataFile(file);
			if (bars.isEmpty() || (bars.lastKey().isBefore(System.currentTimeMillis()) && !nextFile.exists())) {
				int size = bars.size();
				bars.putAll(fetchHistoricalData(symbol, bars.isEmpty() ? month : bars.lastKey(), nextMonth, sourceBarSizeSeconds));
				if (bars.size() != size) {
					writeHistoricalDataFile(bars, file);
				}
			}
			result.putAll(bars.subMap(fromDate, true, toDate, true));
			month = nextMonth;
		}
		if (result.isEmpty() || sourceBarSizeSeconds == barSizeSeconds) {
			return result;
		}
		return convertBarData(result, barSizeSeconds);
	}

	private NavigableMap<DateTime, Bar> convertBarData(NavigableMap<DateTime, Bar> bars, int barSizeSeconds) {
		Bar firstBar = bars.get(bars.firstKey());
		DateTimeZone tz = firstBar.getDateTime().getZone();
		Symbol symbol = firstBar.getSymbol();
		long barSizeMillis = barSizeSeconds * 1000;
		Duration barSize = new Duration(barSizeMillis);
		Bar currBar = null;
		Bar prevBar = null;
		double open = 0.0, high = Double.NEGATIVE_INFINITY, low = Double.POSITIVE_INFINITY, close = 0.0, wap = 0.0;
		int trades = 0;
		long volume = 0;
		NavigableMap<DateTime, Bar> result = new TreeMap<DateTime, Bar>();
		for (Bar bar : bars.values()) {
			long millis = bar.getDateTime().getMillis();
			boolean complete = false;
			if (currBar == null) {
				currBar = new Bar(barSize, symbol, new DateTime(millis - (millis % barSizeMillis) - tz.getOffset(millis), tz));
			} else if (currBar.getDateTime().getMillis() + barSizeMillis <= millis) {
				prevBar = currBar;
				currBar = new Bar(barSize, symbol, new DateTime(millis - (millis % barSizeMillis) - tz.getOffset(millis), tz));
				complete = true;
			}
			if (open == 0.0) {
				open = bar.getOpen();
			}
			if (bar.getHigh() > high) {
				high = bar.getHigh();
			}
			if (bar.getLow() < low) {
				low = bar.getLow();
			}
			close = bar.getClose();
			wap = Util.round((wap * volume + bar.getPrice() * bar.getVolume()) / (volume + bar.getVolume()), 2);
			volume += bar.getVolume();
			trades += bar.getTrades();
			currBar.setValues(open, high, low, close, wap, volume, trades);
			if (complete) {
				result.put(prevBar.getDateTime(), prevBar);
				open = close = wap = 0.0;
				high = Double.NEGATIVE_INFINITY;
				low = Double.POSITIVE_INFINITY;
				volume = trades = 0;
			}
		}
		if (currBar.isComplete()) {
			result.put(currBar.getDateTime(), currBar);
		}
		bars.clear();
		return result;
	}

	@Override
	public NavigableMap<DateTime, Tick> getTickData(Symbol symbol, DateTime fromDate, DateTime toDate) {
		NavigableMap<DateTime, Tick> result = new TreeMap<DateTime, Tick>();
		DateTime month = fromDate.withDayOfMonth(1).withTime(0, 0, 0, 0);

		while (toDate.isAfter(month)) {
			if (symbol.getExpiry() != null && month.isAfter(symbol.getExpiry())) {
				break;
			}
			File file = MarketDataIO.createReadFile(symbol, month, dataDir);
			month = month.plusMonths(1);
			if (!file.exists()) {
				continue;
			}
			NavigableMap<DateTime, Tick> ticks = readHistoricalTickDataFile(file).subMap(fromDate, true, toDate, true);
			result.putAll(ticks);
		}
		return result;
	}

	protected abstract NavigableMap<DateTime, Bar> fetchHistoricalData(Symbol symbol, DateTime fromDate, DateTime toDate, int barSizeSeconds);

	protected NavigableMap<DateTime, Bar> readHistoricalDataFile(File file) {
		if (!file.exists()) {
			return new TreeMap<DateTime, Bar>();
		}
		try {
			return MarketDataIO.createBarReader(file).readBars();
		} catch (IOException e) {
			throw new JTradeException("" + file.getAbsolutePath(), e);
		}
	}

	protected NavigableMap<DateTime, Tick> readHistoricalTickDataFile(File file) {
		if (!file.exists()) {
			return new TreeMap<DateTime, Tick>();
		}
		try {
			return MarketDataIO.createTickReader(file, true).readTicks();
		} catch (IOException e) {
			throw new JTradeException("" + file.getAbsolutePath(), e);
		}
	}

	protected void writeHistoricalDataFile(NavigableMap<DateTime, Bar> bars, File file) {
		if (bars.isEmpty()) {
			return;
		}
		try {
			File f = MarketDataIO.createWriteFile(bars.firstEntry().getValue(), dataDir);
			MarketDataIO.createBarWriter(f, false, getClass().getSimpleName()).writeAll(bars);
		} catch (IOException e) {
			logger.error(e.getMessage() + ": " + file.getAbsolutePath(), e);
		}
	}

	@Override
	public void removeListener(Object listener) {
		if (listener instanceof MarketListener) {
			removeMarketListener((MarketListener) listener);
		}
		if (listener instanceof BarListener) {
			removeBarListener((BarListener) listener);
		}
		if (listener instanceof TickListener) {
			removeTickListener((TickListener) listener);
		}
	}

	@Override
	public synchronized void removeAllListeners() {
		marketListeners.clear();
	}
}
