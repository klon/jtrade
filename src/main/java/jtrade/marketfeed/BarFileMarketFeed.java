package jtrade.marketfeed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jtrade.JTradeException;
import jtrade.Symbol;
import jtrade.io.BarReader;
import jtrade.io.MarketDataIO;
import jtrade.util.Configurable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BarFileMarketFeed extends FileMarketFeed {
	private static final Logger logger = LoggerFactory.getLogger(BarFileMarketFeed.class);
	public static final Configurable<Boolean> USE_TICK_DATA = new Configurable<Boolean>("USE_TICK_DATA", false);
	public static final Configurable<Integer> BAR_SIZE = new Configurable<Integer>("BAR_SIZE", Integer.valueOf(60));

	protected SynchronizedBarDataReader barDataReader;
	protected int barSizeSeconds;
	protected boolean useTickData;
	protected Class<? extends BarDataFileReader> barDataFileReaderClass;
	protected List<BarListener> barListeners;

	public BarFileMarketFeed() {
		this(null, null, null, USE_TICK_DATA.get(), BAR_SIZE.get(), (Symbol[]) null);
	}

	public BarFileMarketFeed(File dataDir) {
		this(dataDir, null, null, USE_TICK_DATA.get(), BAR_SIZE.get(), (Symbol[]) null);
	}

	public BarFileMarketFeed(String dataDir) {
		this(new File(dataDir), null, null, USE_TICK_DATA.get(), BAR_SIZE.get(), (Symbol[]) null);
	}

	public BarFileMarketFeed(File dataDir, DateTime fromDate, DateTime toDate) {
		this(dataDir, fromDate, toDate, USE_TICK_DATA.get(), BAR_SIZE.get(), (Symbol[]) null);
	}

	public BarFileMarketFeed(BarFileMarketFeed marketFeed) {
		this(marketFeed.dataDir, marketFeed.fromDate, marketFeed.toDate, marketFeed.useTickData, marketFeed.barSizeSeconds, marketFeed.symbols);
	}

	public BarFileMarketFeed(String dataDir, String fromDate, String toDate, boolean useTickData, int barSizeSeconds, String... symbols) {
		this(new File(dataDir), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(fromDate), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(toDate),
				useTickData, barSizeSeconds, parseSymbols(symbols));
	}

	public BarFileMarketFeed(String dataDir, String fromDate, String toDate, int barSizeSeconds, String... symbols) {
		this(new File(dataDir), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(fromDate), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(toDate),
				USE_TICK_DATA.get(), barSizeSeconds, parseSymbols(symbols));
	}

	public BarFileMarketFeed(String dataDir, String fromDate, String toDate, String... symbols) {
		this(new File(dataDir), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(fromDate), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(toDate),
				USE_TICK_DATA.get(), BAR_SIZE.get(), parseSymbols(symbols));
	}

	public BarFileMarketFeed(String dataDir, String fromDate, String toDate, List<Symbol> symbols) {
		this(new File(dataDir), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(fromDate), DateTimeFormat.forPattern("yyyyMMdd").parseDateTime(toDate),
				USE_TICK_DATA.get(), BAR_SIZE.get(), symbols.toArray(new Symbol[symbols.size()]));
	}

	public BarFileMarketFeed(File dataDir, DateTime fromDate, DateTime toDate, Symbol... symbols) {
		this(dataDir, fromDate, toDate, USE_TICK_DATA.get(), BAR_SIZE.get(), symbols);
	}

	public BarFileMarketFeed(File dataDir, DateTime fromDate, DateTime toDate, boolean useTickData, int barSizeSeconds, Symbol... symbols) {
		super(dataDir);
		this.barDataFileReaderClass = DefaultBarDataFileReader.class;
		this.barSizeSeconds = barSizeSeconds;
		this.useTickData = useTickData;
		barListeners = new ArrayList<BarListener>();
		reset(fromDate, toDate, symbols);
	}

	@Override
	public void reset(DateTime fromDate, DateTime toDate, Symbol... symbols) {
		if (isConnected()) {
			disconnect();
		}
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.symbols = symbols;
		filesBySymbol = findSymbolFiles(symbols, fromDate, toDate, barSizeSeconds, useTickData);
		barDataReader = new SynchronizedBarDataReader();
		barListeners.clear();

		if (fromDate != null && toDate != null) {
			logger.info(String.format("Initialized market feed, from %s to %s using data folder \"%s\": %s", fromDate.toString("yyyy-MM-dd"),
					toDate.toString("yyyy-MM-dd"), dataDir, filesBySymbol.toString()));
		} else {
			logger.info(String.format("Initialized market feed using data folder \"%s\": %s", dataDir, filesBySymbol.toString()));
		}
	}

	@Override
	public void doConnect() {
		new Thread(barDataReader).start();
	}

	@Override
	public void doDisconnect() {
		barDataReader.stop();
		try {
			while (!barDataReader.isDone()) {
				Thread.sleep(50);
			}
		} catch (InterruptedException e) {
		}
	}

	@Override
	public void removeAllListeners() {
		super.removeAllListeners();
		for (BarDataFileReader r : barDataReader.readers) {
			r.listeners.clear();
		}
		barListeners.clear();
	}

	@Override
	public Tick getLastTick(Symbol symbol) {
		Bar bar = getLastBar(symbol);
		if (bar == null) {
			return null;
		}
		Tick tick = new Tick(symbol);
		tick.dateTime = bar.getDateTime();
		// tick.price = (bar.getHigh() + bar.getLow()) / 2;
		tick.price = bar.getClose();
		// tick.ask = bar.getClose();
		tick.ask = bar.getClose() + bar.getSymbol().getMinTick() * 1.0;
		// tick.ask = bar.getHigh();
		// tick.ask = tick.price + 0.25;
		// tick.ask = tick.price * 1.0005;
		// tick.bid = bar.getLow();
		// tick.bid = tick.price - 0.25;
		// tick.bid = tick.price * 0.9995;
		tick.bid = bar.getClose() - bar.getSymbol().getMinTick() * 1.0;
		// tick.bid = bar.getClose();
		tick.askSize = 0;
		tick.bidSize = 0;
		tick.lastSize = bar.getTrades() > 0 ? 1 : 0;
		return tick;
	}

	@Override
	public Bar getLastBar(Symbol symbol) {
		BarDataFileReader r = barDataReader.readersBySymbol.get(symbol);
		if (r == null) {
			return null;
		}
		long currMillis = barDataReader.currDateTime.getMillis();
		if (r.currBar != null && r.currBar.dateTime.getMillis() <= currMillis) {
			return r.currBar;
		}
		if (r.prevBar != null && r.prevBar.dateTime.getMillis() <= currMillis) {
			return r.prevBar;
		}
		return null;
	}

	@Override
	public void addBarListener(BarListener listener) {
		barListeners.add(listener);
	}

	@Override
	public void removeBarListener(BarListener listener) {
		barListeners.remove(listener);
		for (BarDataFileReader reader : barDataReader.readersBySymbol.values()) {
			removeBarListener(reader.symbol, listener);
		}
	}

	@Override
	public void addBarListener(Symbol symbol, BarListener listener) {
		addBarListener(symbol, listener, barSizeSeconds, null);
	}

	@Override
	public void addBarListener(Symbol symbol, BarListener listener, int barSizeSeconds, Cleaner cleaner) {
		if (this.barSizeSeconds != barSizeSeconds) {
			throw new IllegalArgumentException(String.format("Bar size does not match this feeds bar size: %s", barSizeSeconds));
		}
		BarDataFileReader reader = barDataReader.readersBySymbol.get(symbol);
		if (reader == null) {
			throw new IllegalArgumentException(String.format("Symbol has no matching data file: %s", symbol));
		}
		if (reader.cleaner == null && cleaner != null) {
			reader.cleaner = cleaner;
		}
		if (!reader.listeners.contains(listener)) {
			reader.listeners.add(listener);
		}
	}

	@Override
	public void removeBarListener(Symbol symbol, BarListener listener) {
		BarDataFileReader reader = barDataReader.readersBySymbol.get(symbol);
		if (reader != null) {
			reader.listeners.remove(listener);
			if (reader.listeners.isEmpty() && reader.cleaner != null) {
				reader.cleaner.reset();
			}
		}
	}

	private void fireBarEvent(BarDataFileReader reader) {
		boolean valid = isValidBar(reader);
		if (!valid && reader.prevBar != null && reader.prevBar.close > 0.0) {
			reader.currBar.open = reader.currBar.high = reader.currBar.low = reader.currBar.close = reader.currBar.wap = reader.prevBar.close;
			reader.currBar.volume = 0;
			reader.currBar.trades = 0;
			valid = true;
		}
		if (valid) {
			int len = reader.listeners.size();
			for (int i = 0; i < len; i++) {
				try {
					reader.listeners.get(i).onBar(reader.currBar);
				} catch (Throwable t) {
					logger.error(t.getMessage(), t);
				}
			}
			len = barListeners.size();
			for (int i = 0; i < len; i++) {
				try {
					barListeners.get(i).onBar(reader.currBar);
				} catch (Throwable t) {
					logger.error(t.getMessage(), t);
				}
			}
		}
	}

	private boolean isValidBar(BarDataFileReader reader) {
		Bar curr = reader.currBar;
		if (curr.close <= 0 || curr.high <= 0 || curr.low <= 0 || curr.close <= 0) {
			return false;
		}
		if (curr.high < curr.low) {
			return false;
		}
		if (reader.cleaner != null && Double.isNaN(reader.cleaner.update(curr.getDateTime(), curr.getPrice()))) {
			return false;
		}
		return true;
	}

	final class SynchronizedBarDataReader implements Runnable {
		Map<Symbol, BarDataFileReader> readersBySymbol;
		LinkedList<BarDataFileReader> readers;
		DateTime currDateTime;
		boolean running;
		boolean done;

		SynchronizedBarDataReader() {
			this.readersBySymbol = new LinkedHashMap<Symbol, BarDataFileReader>();
			this.readers = new LinkedList<BarDataFileReader>();
			for (Entry<Symbol, List<File>> entry : filesBySymbol.entrySet()) {
				try {
					BarDataFileReader context = (BarDataFileReader) BarFileMarketFeed.this.barDataFileReaderClass.getConstructors()[0].newInstance(
							BarFileMarketFeed.this, entry.getKey(), entry.getValue());
					readersBySymbol.put(entry.getKey(), context);
				} catch (Exception e) {
					throw new JTradeException(e);
				}
			}
		}

		public void stop() {
			running = false;
		}

		public boolean isDone() {
			return done;
		}

		@Override
		public void run() {
			long start = System.currentTimeMillis();
			running = true;
			try {
				Bar b = null;
				DateTime nextDay = null;
				DateTime nextHour = null;
				DateTime nextMinute = null;

				for (BarDataFileReader reader : readersBySymbol.values()) {
					reader.openNextFile();
					while ((b = reader.nextBar()) != null && !fromDate.isBefore(b.getDateTime())) {
					}
					if (b == null) {
						reader.reader.close();
						continue;
					}
					nextMinute = (nextMinute == null || b.getDateTime().isBefore(nextMinute) ? b.getDateTime() : nextMinute);
					readers.add(reader);
				}
				if (readers.isEmpty()) {
					return;
				}

				while (running && !readers.isEmpty()) {
					Collections.sort(readers);
					BarDataFileReader reader = readers.removeFirst();
					b = reader.currBar;

					nextDay = nextMinute.dayOfMonth().roundCeilingCopy();
					nextHour = nextMinute.hourOfDay().roundCeilingCopy();
					nextMinute = nextMinute.minuteOfHour().roundCeilingCopy();

					while (nextMinute.isBefore(b.dateTime) || nextMinute.isEqual(b.dateTime)) {
						currDateTime = nextMinute;
						fireMinuteEvent(nextMinute);
						if (nextHour.isBefore(nextMinute) || nextHour.isEqual(nextMinute)) {
							fireHourEvent(nextHour);
							if (nextDay.isBefore(nextHour) || nextDay.isEqual(nextHour)) {
								fireDayEvent(nextDay);
								nextDay = nextDay.plusDays(1);
							}
							nextHour = nextHour.plusHours(1);
						}
						nextMinute = nextMinute.plusMinutes(1);
					}
					currDateTime = b.dateTime;

					fireBarEvent(reader);

					// processOpenOrders(b.dateTime);

					b = reader.nextBar();
					if (b == null || b.getDateTime().isAfter(toDate)) {
						reader.reader.close();
						continue;
					}
					readers.add(reader);
				}

			} catch (Throwable t) {
				logger.error(t.getMessage(), t);
			} finally {
				for (BarDataFileReader reader : readers) {
					try {
						reader.reader.close();
					} catch (Exception e) {
					}
				}
				readers.clear();
				done = true;
				if (logger.isDebugEnabled()) {
					logger.debug("run() time: {}", (System.currentTimeMillis() - start));
				}
				for (BarDataFileReader reader : readersBySymbol.values()) {
					reader.reset();
				}
				connected = false;
			}
		}
	}

	public abstract class BarDataFileReader implements Comparable<BarDataFileReader> {
		Symbol symbol;
		List<File> files;
		int next_file;
		BarReader reader;
		Bar currBar;
		Bar prevBar;
		List<BarListener> listeners;
		Cleaner cleaner;

		public BarDataFileReader(Symbol symbol, List<File> files) throws IOException {
			this.symbol = symbol;
			this.files = files;
			this.listeners = new ArrayList<BarListener>();
		}

		public void reset() {
			next_file = 0;
			currBar = null;
			prevBar = null;
			if (cleaner != null) {
				cleaner.reset();
			}
		}

		public abstract void openNextFile() throws IOException;

		public boolean hasNextFile() {
			return next_file < files.size();
		}

		public abstract Bar nextBar() throws IOException;

		@Override
		public int compareTo(BarDataFileReader o) {
			return currBar.dateTime.compareTo(o.currBar.dateTime);
		}

		@Override
		protected void finalize() throws Throwable {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public class DefaultBarDataFileReader extends BarDataFileReader {

		public DefaultBarDataFileReader(Symbol symbol, List<File> file) throws IOException {
			super(symbol, file);
		}

		@Override
		public void openNextFile() throws IOException {
			if (reader != null) {
				reader.close();
			}
			if (useTickData) {
				reader = MarketDataIO.createBarTickReader(files.get(next_file++), barSizeSeconds);
			} else {
				reader = MarketDataIO.createBarReader(files.get(next_file++));
			}
		}

		@Override
		public Bar nextBar() throws IOException {
			while (true) {
				Bar bar = reader.readBar();
				if (bar == null || bar.equals(currBar)) {
					if (hasNextFile()) {
						openNextFile();
						continue;
					}
					return null;
				}
				prevBar = currBar;
				currBar = bar;
				return currBar;
			}
		}
	}

}
